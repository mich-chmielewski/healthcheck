package pl.mgis.restapi.service;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.mgis.restapi.model.HitLog;
import pl.mgis.restapi.model.RequestSchedule;
import pl.mgis.restapi.model.ResponseType;
import pl.mgis.restapi.model.ServiceUrl;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.lang.Thread.currentThread;

@Service
public class ScheduledTasks {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    private final ServiceUrlService serviceUrlService;
    private final HitLogService hitLogService;

    public ScheduledTasks(ServiceUrlService serviceUrlService, HitLogService hitLogService) {
        this.serviceUrlService = serviceUrlService;
        this.hitLogService = hitLogService;
    }

    @Scheduled(fixedRate = 15, timeUnit = TimeUnit.MINUTES)
    public void runEveryFifteenMin() {
        scheduleTasksCronFromDb(RequestSchedule.EVERY_15_MIN);
    }

    @Scheduled(initialDelay = 1, fixedRate = 60, timeUnit = TimeUnit.MINUTES)
    public void runEveryOneHour() {
        scheduleTasksCronFromDb(RequestSchedule.EVERY_1_HOUR);
    }

    @Scheduled(initialDelay = 2, fixedRate = 12 * 60, timeUnit = TimeUnit.MINUTES)
    public void runEveryTwelveHour() {
        scheduleTasksCronFromDb(RequestSchedule.EVERY_12_HOUR);
    }

    @Scheduled(initialDelay = 3, fixedRate = 24 * 60, timeUnit = TimeUnit.MINUTES)
    public void runEveryDay() {
        scheduleTasksCronFromDb(RequestSchedule.EVERY_DAY);
    }

    public void scheduleTasksCronFromDb(RequestSchedule requestSchedule) {
        logger.info("Schedule {} executed at {}", requestSchedule, LocalDateTime.now());
        List<ServiceUrl> serviceUrlList = serviceUrlService.findAll().stream().
                filter(u -> u.getRequestSchedule().equals(requestSchedule)).collect(Collectors.toList());
        OkHttpClient okClient = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
        for (ServiceUrl s : serviceUrlList) {
            try {
                Response response = okClient.newCall(getOkRequest(s)).execute();
                if (response.code() >= 400) {
                    logger.info("{} {}- {} Service UP but with ERRORS {}", currentThread().getName(),
                            s.getRequestSchedule(), LocalDateTime.now(), s.getUrlAddress());
                    logger.info("BODY {}", response.peekBody(200).string());
                    createHitLog(s, response);
                    continue;
                }
                logger.info("{} Service UP {}", LocalDateTime.now(), s.getUrlAddress());
                if (!s.getResponseType().equals(ResponseType.NO_CHECK))
                    logger.info("BODY: {}", String.format("%.200s...",response.peekBody(200).string()));
            } catch (IOException | NullPointerException e) {
                logger.info("{} {}- {} Service DOWN {}", currentThread().getName(), s.getRequestSchedule(),
                        LocalDateTime.now(), s.getUrlAddress());
                Response response = new Response.Builder()
                        .code(503) // status code
                        .request(getOkRequest(s))
                        .protocol(Protocol.HTTP_1_1)
                        .message("Server error")
                        .body(ResponseBody.create("Service DOWN", MediaType.get("text/plain; charset=utf-8")
                        ))
                        .build();
                createHitLog(s, response);
            }
        }
    }

    private void createHitLog(ServiceUrl s, Response response) {
        HitLog hitLog = new HitLog();
        hitLog.setCreated(LocalDateTime.now());
        hitLog.setResponseStatus(response.code());
        try {
            hitLog.setResponseBody(String.format("%.200s...",response.peekBody(200).string()));
        } catch (IOException e) {
            hitLog.setResponseBody(e.getMessage());
        }
        hitLog.setServiceUrl(s);
        hitLogService.save(hitLog);
    }

    private HttpRequest getHttpRequest(ServiceUrl s) {
        return HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(URI.create(s.getUrlAddress()))
                .timeout(Duration.ofMinutes(1))
                .GET()
                .build();
    }

    private Request getOkRequest(ServiceUrl s) {
        return new Request.Builder()
                .url(s.getUrlAddress())
                .get()
                .build();
    }
}
