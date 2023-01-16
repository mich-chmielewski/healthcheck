package pl.mgis.healthcheck.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.mgis.healthcheck.model.ServiceUrl;

import java.util.List;
import java.util.Set;

@Repository
public interface ServiceUrlRepository extends JpaRepository<ServiceUrl, Long> {


    @Query("select distinct u from ServiceUrl u"
            + " left join fetch u.hitLogs"
            + " where lower(u.urlAddress) like %:address%")
    List<ServiceUrl> findAllUrlsContainingString(@Param(value ="address") String address);

    @Query("select u from ServiceUrl u"
            + " left join fetch u.hitLogs")
    Set<ServiceUrl> findAllServiceUrl();

    @Query("select u from ServiceUrl u"
            + " left join fetch u.hitLogs")
    List<ServiceUrl> findAllPaged(Pageable pageable);


}
