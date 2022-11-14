package pl.mgis.restapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.mgis.restapi.model.HitLog;
import pl.mgis.restapi.model.ResponseType;
import pl.mgis.restapi.model.ServiceUrl;
import pl.mgis.restapi.repository.HitLogRepository;
import pl.mgis.restapi.repository.ServiceUrlRepository;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.channels.UnresolvedAddressException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


@Component
public class ScheduledTasks {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    private final ServiceUrlRepository serviceUrlRepository;
    private final HitLogService hitLogService;

    public ScheduledTasks(ServiceUrlRepository serviceUrlRepository, HitLogService hitLogService) {
        this.serviceUrlRepository = serviceUrlRepository;
        this.hitLogService = hitLogService;
    }

    @Scheduled(cron = "0 */5 * ? * *")
    public void scheduleTasksCronFromDb() {
        logger.info("Service executed at {}", LocalDateTime.now());
        List<ServiceUrl> serviceUrlList = serviceUrlRepository.findAll();
        HttpClient client = HttpClient.newHttpClient();
        for (ServiceUrl s : serviceUrlList) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .version(HttpClient.Version.HTTP_1_1)
                        .uri(URI.create(s.getUrlAddress()))
                        .timeout(Duration.ofMinutes(1))
                        .GET()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                int responseCode = response.statusCode();
                if (responseCode >= 400) {
                    logger.info("{} Service UP but with ERRORS {}", LocalDateTime.now(), s.getUrlAddress());
                    logger.info("BODY {}", response.body());
                    HitLog hitLog = new HitLog();
                    hitLog.setCreated(LocalDateTime.now());
                    hitLog.setResponseStatus(responseCode);
                    hitLog.setResponseBody(response.body());
                    hitLog.setServiceUrl(s);
                    hitLogService.save(hitLog);
                    continue;
                }
                logger.info("{} Service UP {}", LocalDateTime.now(), s.getUrlAddress());
                if (!s.getResponseType().equals(ResponseType.NO_CHECK))
                    logger.info("BODY: {}", response.body());
            } catch (InterruptedException | IOException | UnresolvedAddressException e) {
                logger.info("{} Service DOWN {}", LocalDateTime.now() ,s.getUrlAddress());
                HitLog hitLog = new HitLog();
                hitLog.setCreated(LocalDateTime.now());
                hitLog.setResponseStatus(500);
                hitLog.setResponseBody(
                        String.format("%s Service DOWN %s. Stack: %s", LocalDateTime.now()
                        ,s.getUrlAddress(),e.getMessage())
                );
                hitLog.setServiceUrl(s);
                hitLogService.save(hitLog);
            }
        }
    }
}
