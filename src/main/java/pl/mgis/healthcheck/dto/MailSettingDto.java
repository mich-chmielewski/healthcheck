package pl.mgis.healthcheck.dto;

public record MailSettingDto(Long id, int smtpPort, String hostAddress, String fromAddress,
                             String protocol, String username, String password, String recipient, boolean verified) {
}
