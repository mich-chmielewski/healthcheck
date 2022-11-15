package pl.mgis.restapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.mgis.restapi.model.HitLog;
import pl.mgis.restapi.model.MailSetting;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Service
public class ScheduledMailNotification {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledMailNotification.class);

    private final EmailService emailService;
    private final HitLogService hitLogService;

    public ScheduledMailNotification(EmailService emailService, HitLogService hitLogService) {
        this.emailService = emailService;
        this.hitLogService = hitLogService;
    }

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    public void sendScheduledEmailNotifications(){
        List<HitLog> todayHitLog = hitLogService.findTodayHitLog();
        List<Long> ids = new ArrayList<>();
        for (HitLog h : todayHitLog) {
            String subject = h.getResponseStatus() == 503 ? "Service DOWN "  : "Service PROBLEM ";
            sendSimpleMessage(subject + h.getServiceUrl().getUrlAddress(), h.toString());
        }
    }

    public void sendSimpleMessage(String subject, String message) {
        JavaMailSenderImpl mailSender;
        MailSetting mailSetting = emailService.getEmailSetting();
        try {
            mailSender = mailSender();
        } catch (Exception e) {
            logger.info("No email notification message send!");
            logger.info("Some e-mail properties are missing or are incorrect: {}", e.getMessage());
            return;
        }
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(mailSetting.getFromAddress());
            helper.setTo(mailSetting.getRecipient());
            helper.setSubject(subject);
            helper.setText(message, true);
            mailSender.send(mimeMessage);
        } catch (RuntimeException | MessagingException e) {
            System.out.println(e.getMessage());
        }
    }

    public JavaMailSenderImpl mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        MailSetting mailSetting = emailService.getEmailSetting();
        mailSender.setHost(mailSetting.getHostAddress());
        mailSender.setPort(mailSetting.getSmtpPort());

        mailSender.setUsername(mailSetting.getUsername());
        mailSender.setPassword(mailSetting.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", mailSetting.getProtocol());
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false");
        return mailSender;
    }
}
