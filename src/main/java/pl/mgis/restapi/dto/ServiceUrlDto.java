package pl.mgis.restapi.dto;

import pl.mgis.restapi.model.ResponseType;

public record ServiceUrlDto(Long id, String urlAddress, ResponseType responseType,int hitIntervalInMinutes) {
}
