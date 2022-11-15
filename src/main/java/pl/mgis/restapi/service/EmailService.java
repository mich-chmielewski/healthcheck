package pl.mgis.restapi.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pl.mgis.restapi.model.MailSetting;
import pl.mgis.restapi.repository.EmailRepository;

import java.util.Optional;

@Service
public class EmailService {

    private final EmailRepository emailRepository;
    private final HitLogService hitLogService;


    public EmailService(EmailRepository emailRepository, HitLogService hitLogService) {
        this.emailRepository = emailRepository;
        this.hitLogService = hitLogService;
    }

    @Cacheable("getEmailSetting")
    public MailSetting getEmailSetting() {
        Optional<MailSetting> first = emailRepository.findAll().stream().findFirst();
        return first.orElseGet(MailSetting::new);

    }
}
