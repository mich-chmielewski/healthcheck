package pl.mgis.restapi.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.mgis.restapi.dto.Mapper;
import pl.mgis.restapi.dto.ServiceUrlDto;
import pl.mgis.restapi.model.HitLog;
import pl.mgis.restapi.model.ServiceUrl;
import pl.mgis.restapi.repository.HitLogRepository;
import pl.mgis.restapi.repository.ServiceUrlRepository;

import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ServiceUrlService {

    private static final int PAGE_SIZE = 10;

    private final ServiceUrlRepository urlRepository;
    private final HitLogRepository hitLogRepository;
    private final EntityManager em;

    public ServiceUrlService(ServiceUrlRepository urlRepository, HitLogRepository hitLogRepository, EntityManager em) {
        this.urlRepository = urlRepository;
        this.hitLogRepository = hitLogRepository;
        this.em = em;
    }


    public Set<ServiceUrlDto> getAll() {

        return urlRepository.findAllServiceUrl()
                .stream().map(u -> new ServiceUrlDto(u.getId(), u.getUrlAddress()
                        , u.getResponseType(), u.getHitIntervalInMinutes(),
                        u.getHitLogs().stream().map(Mapper::hitLogToDto).collect(Collectors.toSet())))
                .collect(Collectors.toSet());
    }

    public List<ServiceUrlDto> getAllPaged(int page, Sort.Direction sort) {
        List<ServiceUrl> serviceUrlList = urlRepository.findAllPaged(PageRequest.of(page, PAGE_SIZE, Sort.by(sort, "id")));
        List<Long> ids = serviceUrlList.stream().map(ServiceUrl::getId).collect(Collectors.toList());
        List<HitLog> hitLogList = hitLogRepository.findAllByServiceUrlId(ids);
        serviceUrlList.forEach(
                u -> u.setHitLogs(
                        hitLogList.stream().
                                filter(h -> h.getServiceUrl().getId().equals(u.getId()))
                                .collect(Collectors.toSet())
                )
        );
        return serviceUrlList.stream()
                .map(u -> new ServiceUrlDto(u.getId(), u.getUrlAddress(), u.getResponseType()
                        , u.getHitIntervalInMinutes(), u.getHitLogs().stream()
                        .map(Mapper::hitLogToDto).collect(Collectors.toSet())))
                .collect(Collectors.toList());
    }

    public List<ServiceUrl> getAllUrlsContainingString(String urlPart) {
        return urlRepository.findAllUrlsContainingString(urlPart);
    }

    public ServiceUrlDto add(ServiceUrlDto serviceUrlDto) {
        ServiceUrl serviceUrl = new ServiceUrl();
        serviceUrl.setUrlAddress(serviceUrlDto.urlAddress());
        serviceUrl.setHitIntervalInMinutes(serviceUrlDto.hitIntervalInMinutes());
        serviceUrl.setResponseType(serviceUrlDto.responseType());
        ServiceUrl saved = urlRepository.save(serviceUrl);
        return Mapper.serviceUrlToDto(saved);
    }

    @Transactional
    public ServiceUrlDto edit(ServiceUrlDto serviceUrlDto) {
        ServiceUrl serviceUrl = urlRepository.findById(serviceUrlDto.id()).orElseThrow(null);
        serviceUrl.setUrlAddress(serviceUrlDto.urlAddress());
        serviceUrl.setHitIntervalInMinutes(serviceUrlDto.hitIntervalInMinutes());
        serviceUrl.setResponseType(serviceUrlDto.responseType());
        return Mapper.serviceUrlToDto(serviceUrl);
    }

    @Transactional
    public ServiceUrlDto addByHyper(ServiceUrlDto serviceUrlDto) {
        ServiceUrl serviceUrl = new ServiceUrl();
        serviceUrl.setUrlAddress(serviceUrlDto.urlAddress());
        serviceUrl.setHitIntervalInMinutes(serviceUrlDto.hitIntervalInMinutes());
        serviceUrl.setResponseType(serviceUrlDto.responseType());
        em.persist(serviceUrl);
        em.flush();
        return new ServiceUrlDto(serviceUrl.getId(), serviceUrl.getUrlAddress(), serviceUrl.getResponseType(), serviceUrl.getHitIntervalInMinutes(), new HashSet<>());
    }

    public void delete(Long id) {
        urlRepository.deleteById(id);
    }
}
