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

    @Query(value = "SELECT H.* FROM HIT_LOG H LEFT JOIN " +
            "(SELECT COUNT(*)  AS HIT_NUMBER, SERVICE_URL_ID " +
            "FROM HIT_LOG WHERE FORMATDATETIME(CREATED,'yyyy-MM-dd ') = CURRENT_DATE() GROUP BY SERVICE_URL_ID) B " +
            "ON H.SERVICE_URL_ID = B.SERVICE_URL_ID " +
            "WHERE B.HIT_NUMBER < 4", nativeQuery = true)
    List<HitLog> findHitLogsFromPresentDayLessThenFour();
}
