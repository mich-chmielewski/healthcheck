package pl.mgis.healthcheck.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.mgis.healthcheck.dto.Mapper;
import pl.mgis.healthcheck.dto.ServiceUrlDto;
import pl.mgis.healthcheck.model.HitLog;
import pl.mgis.healthcheck.model.ServiceUrl;
import pl.mgis.healthcheck.repository.HitLogRepository;
import pl.mgis.healthcheck.repository.ServiceUrlRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ServiceUrlService {

    private static final int PAGE_SIZE = 10;

    private final ServiceUrlRepository urlRepository;
    private final HitLogRepository hitLogRepository;

    public ServiceUrlService(ServiceUrlRepository urlRepository, HitLogRepository hitLogRepository) {
        this.urlRepository = urlRepository;
        this.hitLogRepository = hitLogRepository;
    }

    @Cacheable("findAllForScheduler")
    public List<ServiceUrl> findAll() {
        return urlRepository.findAll();
    }

    public Set<ServiceUrlDto> getAll() {

        return urlRepository.findAllServiceUrl()
                .stream().map(u -> new ServiceUrlDto(u.getId(), u.getUrlAddress()
                        , u.getResponseType(), u.getRequestSchedule(),
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
                        , u.getRequestSchedule(), u.getHitLogs().stream()
                        .map(Mapper::hitLogToDto).collect(Collectors.toSet())))
                .collect(Collectors.toList());
    }

    public List<ServiceUrl> getAllUrlsContainingString(String urlPart) {
        return urlRepository.findAllUrlsContainingString(urlPart);
    }

    @CacheEvict(value = "findAllForScheduler", allEntries = true)
    public ServiceUrlDto add(ServiceUrlDto serviceUrlDto) {
        ServiceUrl saved = urlRepository.save(Mapper.serviceUrlFromDto(serviceUrlDto));
        return Mapper.serviceUrlToDto(saved);
    }

    @Transactional
    @CacheEvict(value = "findAllForScheduler", allEntries = true)
    public ServiceUrlDto edit(ServiceUrlDto serviceUrlDto) {
        ServiceUrl serviceUrl = urlRepository.findById(serviceUrlDto.id()).orElseThrow(null);
        serviceUrl.setUrlAddress(serviceUrlDto.urlAddress());
        serviceUrl.setRequestSchedule(serviceUrlDto.requestSchedule());
        serviceUrl.setResponseType(serviceUrlDto.responseType());
        return Mapper.serviceUrlToDto(serviceUrl);
    }

    @CacheEvict(value = "findAllForScheduler", allEntries = true)
    public void delete(Long id) {
        urlRepository.deleteById(id);
    }

    public ServiceUrlDto getServiceById(Long id) {
        Optional<ServiceUrl> urlService = urlRepository.findById(id);
        if (urlService.isPresent()) {
            return Mapper.serviceUrlToDto(urlService.get());
        }
        throw new NoSuchElementException("Element not found");
    }
}
