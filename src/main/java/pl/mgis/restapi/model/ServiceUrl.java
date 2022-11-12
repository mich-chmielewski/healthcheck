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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String urlAddress;
    @Enumerated(EnumType.STRING)
    private ResponseType responseType;
    private int hitIntervalInMinutes;
    @OneToMany(mappedBy = "serviceUrl", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    List<HitLog> hitLogs;
}
