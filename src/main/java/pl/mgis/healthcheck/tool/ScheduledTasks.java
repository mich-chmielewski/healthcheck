package pl.mgis.healthcheck.tool;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.mgis.healthcheck.Mailer.MailerService;
import pl.mgis.healthcheck.dto.HitLogDto;
import pl.mgis.healthcheck.dto.Mapper;
import pl.mgis.healthcheck.model.HitLog;
import pl.mgis.healthcheck.model.RequestSchedule;
import pl.mgis.healthcheck.model.ServiceUrl;
import pl.mgis.healthcheck.service.EmailService;
import pl.mgis.healthcheck.service.HitLogService;
import pl.mgis.healthcheck.service.ServiceUrlService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.lang.Thread.currentThread;

@Slf4j
@Service
public class ScheduledTasks {
    private final ServiceUrlService serviceUrlService;
    private final HitLogService hitLogService;
    private final MailerService mailerService;
    private final EmailService emailService;

    public ScheduledTasks(ServiceUrlService serviceUrlService, HitLogService hitLogService, MailerService mailerService, EmailService emailService) {
        this.serviceUrlService = serviceUrlService;
        this.hitLogService = hitLogService;
        this.mailerService = mailerService;
        this.emailService = emailService;
    }

    //TODO Everything

    @Scheduled(fixedRate = 15, timeUnit = TimeUnit.MINUTES)
    public void runEveryFifteenMin() {
        scheduleTasksCronFromDb(RequestSchedule.EVERY_15_MIN);
    }

    @Scheduled(initialDelay = 2, fixedRate = 60, timeUnit = TimeUnit.MINUTES)
    public void runEveryOneHour() {
        scheduleTasksCronFromDb(RequestSchedule.EVERY_1_HOUR);
    }

    @Scheduled(initialDelay = 4, fixedRate = 12 * 60, timeUnit = TimeUnit.MINUTES)
    public void runEverySixHour() {
        scheduleTasksCronFromDb(RequestSchedule.EVERY_6_HOUR);
    }

    @Scheduled(initialDelay = 6, fixedRate = 12 * 60, timeUnit = TimeUnit.MINUTES)
    public void runEveryTwelveHour() {
        scheduleTasksCronFromDb(RequestSchedule.EVERY_12_HOUR);
    }

    @Scheduled(initialDelay = 8, fixedRate = 24 * 60, timeUnit = TimeUnit.MINUTES)
    public void runEveryTwentyFourHour() {
        scheduleTasksCronFromDb(RequestSchedule.EVERY_24_HOUR);
    }

    public void scheduleTasksCronFromDb(RequestSchedule requestSchedule) {
        log.info("Schedule {} executed at {}", requestSchedule, LocalDateTime.now());

        List<ServiceUrl> serviceUrlList = serviceUrlService.findAll().stream().
                filter(u -> u.getRequestSchedule().equals(requestSchedule)).collect(Collectors.toList());

        OkHttpClient okClient = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        for (ServiceUrl s : serviceUrlList) {

            try (Response response = okClient.newCall(getOkRequest(s)).execute()) {
                //TODO check response mimetype
                if (response.code() >= 400) {
                    createProblemHitLog(s, response);
                    continue;
                }
                long time = response.receivedResponseAtMillis() - response.sentRequestAtMillis();
                log.info("{} Service UP {} ResponseTime {} millis", LocalDateTime.now(), s.getUrlAddress(), time);
                //TODO create notice HitLog about slow response time
            } catch (IOException | NullPointerException e) {
                createServiceDownHitLog(s);
            }
            sendEmailNotifications(s.getId());
        }
    }

    private void createProblemHitLog(ServiceUrl s, Response response) throws IOException {
        log.info("{} {}- {} Service UP but with ERRORS {}", currentThread().getName(),
                s.getRequestSchedule(), LocalDateTime.now(), s.getUrlAddress());
        createHitLog(s, response);
    }

    private void createServiceDownHitLog(ServiceUrl s) {
        log.info("{} {}- {} Service DOWN {}", currentThread().getName(), s.getRequestSchedule(),
                LocalDateTime.now(), s.getUrlAddress());
        Response response = new Response.Builder()
                .code(503)
                .request(getOkRequest(s))
                .protocol(Protocol.HTTP_1_1)
                .message("Server error")
                .body(ResponseBody.create("Service DOWN", MediaType.get("text/plain; charset=utf-8")
                ))
                .build();
        createHitLog(s, response);
    }

    private void createHitLog(ServiceUrl s, Response response) {
        HitLog hitLog = new HitLog();
        hitLog.setCreated(LocalDateTime.now());
        hitLog.setResponseStatus(response.code());
        try {
            hitLog.setResponseBody(String.format("%.200s...", response.peekBody(200).string()));
        } catch (IOException e) {
            hitLog.setResponseBody(e.getMessage());
        }
        hitLog.setServiceUrl(s);
        hitLogService.save(hitLog);
    }

    private Request getOkRequest(ServiceUrl s) {
        return new Request.Builder()
                .url(s.getUrlAddress())
                .get()
                .build();
    }

    private void sendEmailNotifications(long serviceId) {

        List<HitLog> hitLogList = hitLogService.findTodayHitLog().stream()
                .filter(h -> h.getServiceUrl().getId() == serviceId).collect(Collectors.toList());
        if (hitLogList.size() > 3)
            return;

        Optional<HitLogDto> latestHitLog = hitLogList.stream().max(Comparator.comparing(HitLog::getCreated))
                .map(Mapper::hitLogToDto);

        if (latestHitLog.isPresent()) {
            String subject = latestHitLog.get().responseStatus() == 503 ? "Service DOWN " : "Service PROBLEM ";
            mailerService.sendSimpleMessage(emailService.getEmailSetting(),
                    subject + latestHitLog.get().serviceUrl(), latestHitLog.get().toString());
        }
    }

}
