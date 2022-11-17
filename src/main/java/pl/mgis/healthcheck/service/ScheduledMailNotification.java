package pl.mgis.healthcheck.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.mgis.healthcheck.Mailer.MailerService;
import pl.mgis.healthcheck.dto.Mapper;
import pl.mgis.healthcheck.model.HitLog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ScheduledMailNotification {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledMailNotification.class);

    private final EmailService emailService;
    private final HitLogService hitLogService;
    private final MailerService mailerService;

    public ScheduledMailNotification(EmailService emailService, HitLogService hitLogService, MailerService mailerService) {
        this.emailService = emailService;
        this.hitLogService = hitLogService;
        this.mailerService = mailerService;
    }

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    public void sendScheduledEmailNotifications(){
        List<HitLog> todayHitLog = hitLogService.findTodayHitLog();
        List<Long> ids = new ArrayList<>();
        for (HitLog h : todayHitLog) {
            String subject = h.getResponseStatus() == 503 ? "Service DOWN "  : "Service PROBLEM ";
            mailerService.sendSimpleMessage(emailService.getEmailSetting(),
                    subject + h.getServiceUrl().getUrlAddress(), h.toString());
        }
    }
}
