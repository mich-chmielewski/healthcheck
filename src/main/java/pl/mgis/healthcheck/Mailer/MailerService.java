package pl.mgis.healthcheck.Mailer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import pl.mgis.healthcheck.tool.ValueEnDecrypt;
import pl.mgis.healthcheck.model.MailSetting;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Slf4j
@Service
public class MailerService {

    public void sendSimpleMessage(MailSetting mailSetting, String subject, String message) {

        if (!mailSetting.isVerified()) {
            return;
        }

        JavaMailSenderImpl mailSender = mailSender(mailSetting);
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(mailSetting.getFromAddress());
            helper.setTo(mailSetting.getRecipient());
            helper.setSubject(subject);
            helper.setText(message, true);
            mailSender.send(mimeMessage);
            log.info("Email send!");
        } catch (RuntimeException | MessagingException e) {
            log.info("No email notification message send!");
            log.info("Some e-mail properties are missing or are incorrect: {}", e.getMessage());
        }
    }

    public boolean verifyMailSettings(MailSetting mailSetting) {
        JavaMailSenderImpl javaMailSender = mailSender(mailSetting);
        try {
            javaMailSender.testConnection();
            return true;
        } catch (MessagingException e) {
            log.error("Wrong email Settings");
            return false;
        }
    }

    public JavaMailSenderImpl mailSender(MailSetting mailSetting) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailSetting.getHostAddress());
        mailSender.setPort(mailSetting.getSmtpPort());

        mailSender.setUsername(mailSetting.getUsername());
        mailSender.setPassword(ValueEnDecrypt.decrypt(mailSetting.getPassword()));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", mailSetting.getProtocol());
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false");
        return mailSender;
    }
}
