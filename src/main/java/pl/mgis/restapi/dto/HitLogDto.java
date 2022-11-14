package pl.mgis.restapi.dto;

import java.time.LocalDateTime;

public record HitLogDto(Long id, int responseStatus, String responseBody, LocalDateTime created) {
}
