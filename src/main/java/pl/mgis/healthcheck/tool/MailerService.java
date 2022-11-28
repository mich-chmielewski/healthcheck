package pl.mgis.healthcheck.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import pl.mgis.healthcheck.model.MailSetting;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class MailerService {

    private static final Logger logger = LoggerFactory.getLogger(MailerService.class);
    private final KeyCrypt keyCrypt;

    public MailerService(KeyCrypt keyCrypt) {
        this.keyCrypt = keyCrypt;
    }

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
            logger.info("Email send!");
        } catch (RuntimeException | MessagingException e) {
            logger.info("No email notification message send!");
            logger.info("Some e-mail properties are missing or are incorrect: {}", e.getMessage());
        }
    }

    public boolean verifyMailSettings(MailSetting mailSetting) {
        JavaMailSenderImpl javaMailSender = mailSender(mailSetting);
        try {
            javaMailSender.testConnection();
            return true;
        } catch (MessagingException e) {
            logger.error("Wrong email Settings");
            return false;
        }
    }

    public JavaMailSenderImpl mailSender(MailSetting mailSetting) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailSetting.getHostAddress());
        mailSender.setPort(mailSetting.getSmtpPort());

        mailSender.setUsername(mailSetting.getUsername());
        mailSender.setPassword(ValueEnDecrypt.decrypt(mailSetting.getPassword(),keyCrypt.getKeyCrypt()));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", mailSetting.getProtocol());
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false");
        return mailSender;
    }
}
