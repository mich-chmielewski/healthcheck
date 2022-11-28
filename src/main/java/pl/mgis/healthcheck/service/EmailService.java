package pl.mgis.healthcheck.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pl.mgis.healthcheck.tool.KeyCrypt;
import pl.mgis.healthcheck.tool.MailerService;
import pl.mgis.healthcheck.tool.ValueEnDecrypt;
import pl.mgis.healthcheck.dto.MailSettingDto;
import pl.mgis.healthcheck.dto.Mapper;
import pl.mgis.healthcheck.model.MailSetting;
import pl.mgis.healthcheck.repository.EmailRepository;

import java.util.Optional;

@Service
public class EmailService {

    private final EmailRepository emailRepository;
    private final HitLogService hitLogService;
    private final MailerService mailerService;
    private final KeyCrypt keyCrypt;


    public EmailService(EmailRepository emailRepository, HitLogService hitLogService, MailerService mailerService, KeyCrypt keyCrypt) {
        this.emailRepository = emailRepository;
        this.hitLogService = hitLogService;
        this.mailerService = mailerService;
        this.keyCrypt = keyCrypt;
    }

    public MailSettingDto getEmailSettingDto() {
        Optional<MailSetting> first = emailRepository.findAll().stream().findFirst();
        return Mapper.mailSettingToDto(first.orElseGet(MailSetting::new));
    }

    @Cacheable("getEmailSetting")
    public MailSetting getEmailSetting() {
        Optional<MailSetting> first = emailRepository.findAll().stream().findFirst();
        return first.orElseGet(MailSetting::new);
    }

    @CacheEvict(value ="getEmailSetting", allEntries=true)
    public void updateMailSetting(MailSettingDto mailSettingDto){
        MailSetting mailSetting = Mapper.mailSettingFromDto(mailSettingDto);
        mailSetting.setPassword(ValueEnDecrypt.encrypt(mailSettingDto.password(),keyCrypt.getKeyCrypt()));
        mailSetting.setVerified(mailerService.verifyMailSettings(mailSetting));
        emailRepository.save(mailSetting);
    }
}
