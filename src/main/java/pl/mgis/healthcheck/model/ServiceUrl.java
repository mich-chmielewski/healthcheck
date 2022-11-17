package pl.mgis.healthcheck.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
