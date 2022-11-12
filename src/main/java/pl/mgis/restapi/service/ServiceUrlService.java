package pl.mgis.restapi.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.mgis.restapi.dto.ServiceUrlDto;
import pl.mgis.restapi.model.HitLog;
import pl.mgis.restapi.model.ResponseType;
import pl.mgis.restapi.model.ServiceUrl;
import pl.mgis.restapi.repository.HitLogRepository;
import pl.mgis.restapi.repository.ServiceUrlRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceUrlService {

    private static final int PAGE_SIZE = 1;

    private final ServiceUrlRepository urlRepository;
    private final HitLogRepository hitLogRepository;

    public ServiceUrlService(ServiceUrlRepository urlRepository, HitLogRepository hitLogRepository) {
        this.urlRepository = urlRepository;
        this.hitLogRepository = hitLogRepository;
    }


    public List<ServiceUrl> getAll(){
        return urlRepository.findAllServiceUrl();
    }

    public List<ServiceUrl> getAllPaged(int page, Sort.Direction sort){
        List<ServiceUrl> serviceUrlList = urlRepository.findAllPaged(PageRequest.of(page, PAGE_SIZE,Sort.by(sort,"id")));
        List<Long> ids = serviceUrlList.stream().map(ServiceUrl::getId).collect(Collectors.toList());
        //List<HitLog> hitLogList = hitLogRepository.findByServiceUrlIdIn(ids);
        List<HitLog> hitLogList = hitLogRepository.findAllByServiceUrlId(ids);
        serviceUrlList.forEach(
                u->u.setHitLogs(
                        hitLogList.stream().
                        filter(h->h.getServiceUrl().getId().equals(u.getId()))
                        .collect(Collectors.toList())
                )
        );
        return serviceUrlList;
    }

    public List<ServiceUrl> getAllUrlsContainingString(String urlPart){
        return urlRepository.findAllUrlsContainingString(urlPart);
    }

    //@Transactional
    public ServiceUrlDto add(ServiceUrlDto serviceUrlDto){
        ServiceUrl serviceUrl = new ServiceUrl();
        serviceUrl.setUrlAddress(serviceUrlDto.urlAddress());
        serviceUrl.setHitIntervalInMinutes(serviceUrlDto.hitIntervalInMinutes());
        serviceUrl.setResponseType(serviceUrlDto.responseType());
        ServiceUrl saved = urlRepository.save(serviceUrl);
        return new ServiceUrlDto(saved.getId(),saved.getUrlAddress(),saved.getResponseType(),saved.getHitIntervalInMinutes());
    }

    public ServiceUrl addByHyper(ServiceUrl serviceUrl){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pl.mgis.restapi");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(serviceUrl);
        em.getTransaction().commit();
        return serviceUrl;
    }
}
