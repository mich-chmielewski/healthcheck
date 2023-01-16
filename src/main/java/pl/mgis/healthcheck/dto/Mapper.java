package pl.mgis.healthcheck.dto;

import pl.mgis.healthcheck.model.HitLog;
import pl.mgis.healthcheck.model.MailSetting;
import pl.mgis.healthcheck.model.ServiceUrl;

import java.util.HashSet;
import java.util.stream.Collectors;

public class Mapper {

    public static HitLogDto hitLogToDto(HitLog hitLog) {
        return new HitLogDto(hitLog.getId(), hitLog.getResponseStatus(), hitLog.getResponseBody(),
                hitLog.getCreated(),hitLog.getServiceUrl().getUrlAddress());
    }

    public static ServiceUrlDto serviceUrlToDto(ServiceUrl serviceUrl) {
        return new ServiceUrlDto(serviceUrl.getId(), serviceUrl.getUrlAddress(),
                serviceUrl.getResponseType(), serviceUrl.getRequestSchedule(),
                serviceUrl.getHitLogs().stream().map(Mapper::hitLogToDto).collect(Collectors.toSet()));
    }

    public static ServiceUrl serviceUrlFromDto(ServiceUrlDto serviceUrlDto) {
        return new ServiceUrl(serviceUrlDto.id(), serviceUrlDto.urlAddress(),
                serviceUrlDto.responseType(), serviceUrlDto.requestSchedule(),
                new HashSet<>());
    }

    public static MailSettingDto mailSettingToDto(MailSetting mailSetting) {
        return new MailSettingDto(mailSetting.getId(), mailSetting.getSmtpPort(), mailSetting.getHostAddress(),
                mailSetting.getFromAddress(), mailSetting.getProtocol(), mailSetting.getUsername(),
                mailSetting.getPassword(), mailSetting.getRecipient(), mailSetting.isVerified());
    }

    public static MailSetting mailSettingFromDto(MailSettingDto mailSettingDto) {
        return new MailSetting(mailSettingDto.id(), mailSettingDto.smtpPort(), mailSettingDto.hostAddress(),
                mailSettingDto.fromAddress(), mailSettingDto.protocol(), mailSettingDto.username(),
                mailSettingDto.password(), mailSettingDto.recipient(), mailSettingDto.verified());
    }


}
