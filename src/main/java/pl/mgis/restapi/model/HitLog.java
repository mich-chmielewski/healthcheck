package pl.mgis.restapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
public class HitLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String responseStatus;
    private String responseBody;
    @JsonIgnore
    @ManyToOne
    private ServiceUrl serviceUrl;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime created;

}
