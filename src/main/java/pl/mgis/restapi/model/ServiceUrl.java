package pl.mgis.restapi.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
public class ServiceUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String urlAddress;
    @Enumerated(EnumType.STRING)
    private ResponseType responseType;
    private int hitIntervalInMinutes;
    @OneToMany(cascade = {CascadeType.REMOVE,CascadeType.DETACH})
    @JoinColumn(name = "service_url_id", updatable = false,insertable = false)
    Set<HitLog> hitLogs;
}
