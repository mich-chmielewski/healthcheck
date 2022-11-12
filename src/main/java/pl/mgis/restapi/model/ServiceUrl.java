package pl.mgis.restapi.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class ServiceUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String urlAddress;
    @Enumerated
    private ResponseType responseType;
    private int hitIntervalInMinutes;
    @OneToMany(mappedBy = "serviceUrl", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    List<HitLog> hitLogs;
}
