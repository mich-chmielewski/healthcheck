package pl.mgis.restapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.mgis.restapi.model.HitLog;

import java.util.List;

@Repository
public interface HitLogRepository extends JpaRepository<HitLog, Long> {

    @Query("select h from HitLog h where h.serviceUrl.id in ?1")
    //@Query(value = "SELECT * FROM HIT_LOG WHERE SERVICE_URL_ID IN (?1)", nativeQuery = true)
    List<HitLog> findAllByServiceUrlId(List<Long> ids);
    //List<HitLog> findByServiceUrlIdIn(List<Long> ids);
}
