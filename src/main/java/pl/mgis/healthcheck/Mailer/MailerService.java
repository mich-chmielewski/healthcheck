package pl.mgis.healthcheck.Mailer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import pl.mgis.healthcheck.dto.MailSettingDto;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class MailerService {

    private static final Logger logger = LoggerFactory.getLogger(MailerService.class);

    public void sendSimpleMessage(MailSettingDto mailSetting, String subject, String message) {
        if (!mailSetting.verified()) {
            return;
        }
        JavaMailSenderImpl mailSender;
        try {
            mailSender = mailSender(mailSetting);
        } catch (Exception e) {
            logger.info("No email notification message send!");
            logger.info("Some e-mail properties are missing or are incorrect: {}", e.getMessage());
            return;
        }
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(mailSetting.fromAddress());
            helper.setTo(mailSetting.recipient());
            helper.setSubject(subject);
            helper.setText(message, true);
            mailSender.send(mimeMessage);
        } catch (RuntimeException | MessagingException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean verifyMailSettings(MailSettingDto mailSetting) {
        JavaMailSenderImpl javaMailSender = mailSender(mailSetting);
        try {
            javaMailSender.testConnection();
            return true;
        } catch (MessagingException e) {
            logger.error("Wrong email Settings");
            return false;
        }
    }

    public JavaMailSenderImpl mailSender(MailSettingDto mailSetting) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailSetting.hostAddress());
        mailSender.setPort(mailSetting.smtpPort());

        mailSender.setUsername(mailSetting.username());
        mailSender.setPassword(mailSetting.password());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", mailSetting.protocol());
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false");
        return mailSender;
    }
}
