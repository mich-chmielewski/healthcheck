package pl.mgis.healthcheck.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import pl.mgis.healthcheck.model.RequestSchedule;
import pl.mgis.healthcheck.model.ResponseType;

import java.util.Set;

public record ServiceUrlDto(Long id, String urlAddress, ResponseType responseType, RequestSchedule requestSchedule,
                            @JsonInclude(JsonInclude.Include.NON_NULL) Set<HitLogDto> hitLogList) {
}
