package pl.mgis.healthcheck.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
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
