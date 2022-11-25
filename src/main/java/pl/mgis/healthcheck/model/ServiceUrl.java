package pl.mgis.healthcheck.model;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String urlAddress;
    @Enumerated(EnumType.STRING)
    private ResponseType responseType;
    @Enumerated(EnumType.STRING)
    private RequestSchedule requestSchedule;
    @OneToMany(cascade = {CascadeType.REMOVE,CascadeType.DETACH})
    @JoinColumn(name = "service_url_id", updatable = false,insertable = false)
    Set<HitLog> hitLogs = new HashSet<>();
}
