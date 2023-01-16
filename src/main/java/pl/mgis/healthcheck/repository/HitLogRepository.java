package pl.mgis.healthcheck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.mgis.healthcheck.model.HitLog;

import java.util.List;

@Repository
public interface HitLogRepository extends JpaRepository<HitLog, Long> {

    @Query("select h from HitLog h where h.serviceUrl.id in ?1")
    List<HitLog> findAllByServiceUrlId(List<Long> ids);

    @Query(value = "SELECT H.* FROM HIT_LOG H WHERE FORMATDATETIME(CREATED,'yyyy-MM-dd ') = CURRENT_DATE()", nativeQuery = true)
    List<HitLog> findHitLogFromPresentDay();

    @Query(value = "SELECT H.* FROM HIT_LOG H WHERE FORMATDATETIME(CREATED,'yyyy-MM-dd ') = PARSEDATETIME(:fromday,'yyyy-MM-dd')", nativeQuery = true)
    List<HitLog> findAllFromDay(@Param(value = "fromday") String fromDay);
}
