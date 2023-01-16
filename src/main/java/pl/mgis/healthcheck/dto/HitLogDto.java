package pl.mgis.healthcheck.dto;

import java.time.LocalDateTime;

public record HitLogDto(Long id, int responseStatus, String responseBody, LocalDateTime created,String serviceUrl) {
}
