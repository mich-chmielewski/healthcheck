package pl.mgis.healthcheck.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MailSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int smtpPort;
    private String hostAddress;
    private String fromAddress;
    private String protocol;
    private String username;
    private String password;
    private String recipient;
    private boolean verified = false;

}
