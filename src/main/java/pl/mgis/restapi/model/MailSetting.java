package pl.mgis.restapi.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
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

}
