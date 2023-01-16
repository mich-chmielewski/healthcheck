package pl.mgis.healthcheck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.mgis.healthcheck.model.MailSetting;

@Repository
public interface EmailRepository extends JpaRepository<MailSetting, Long> {
}
