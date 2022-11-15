package pl.mgis.restapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
public class HitLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int responseStatus;
    private String responseBody;
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime created;
    @JsonIgnore
    @ManyToOne
    private ServiceUrl serviceUrl;
}
