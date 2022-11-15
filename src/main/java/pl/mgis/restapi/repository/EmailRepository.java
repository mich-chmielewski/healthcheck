package pl.mgis.restapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.mgis.restapi.model.MailSetting;

@Repository
public interface EmailRepository extends JpaRepository<MailSetting, Long> {
}
