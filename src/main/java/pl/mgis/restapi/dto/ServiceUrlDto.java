package pl.mgis.restapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import pl.mgis.restapi.model.ResponseType;

import java.util.Set;

public record ServiceUrlDto(Long id, String urlAddress, ResponseType responseType, int hitIntervalInMinutes,
                            @JsonInclude(JsonInclude.Include.NON_NULL) Set<HitLogDto> hitLogList) {
}
