package pl.mgis.restapi.dto;

import pl.mgis.restapi.model.HitLog;
import pl.mgis.restapi.model.ServiceUrl;

import java.util.stream.Collectors;

public class Mapper {

    public static HitLogDto hitLogToDto(HitLog hitLog){
        return new HitLogDto(hitLog.getId(),hitLog.getResponseStatus(),hitLog.getResponseBody(),
                hitLog.getCreated());
    }
    public static ServiceUrlDto serviceUrlToDto(ServiceUrl serviceUrl){
        return new ServiceUrlDto(serviceUrl.getId(),serviceUrl.getUrlAddress(),
                serviceUrl.getResponseType(), serviceUrl.getRequestSchedule(),
                serviceUrl.getHitLogs().stream().map(Mapper::hitLogToDto).collect(Collectors.toSet()));
    }
}
