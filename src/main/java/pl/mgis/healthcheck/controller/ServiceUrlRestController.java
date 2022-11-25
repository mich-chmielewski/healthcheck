package pl.mgis.healthcheck.controller;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.mgis.healthcheck.dto.ServiceUrlDto;
import pl.mgis.healthcheck.model.ServiceUrl;
import pl.mgis.healthcheck.service.ServiceUrlService;

import java.util.List;

@RestController
public class ServiceUrlRestController {
    private final ServiceUrlService serviceUrlService;

    public ServiceUrlRestController(ServiceUrlService serviceUrlService) {
        this.serviceUrlService = serviceUrlService;
    }

    @GetMapping("/api/services/paged")
    public ResponseEntity<List<ServiceUrlDto>> listAllPaged(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                            @RequestParam(defaultValue = "ASC") Sort.Direction sort) {

        return new ResponseEntity<>(serviceUrlService.getAllPaged(page, sort), HttpStatus.OK);
    }

    @GetMapping("/api/services/{urlPart}")
    public ResponseEntity<List<ServiceUrl>> listAllContainingString(@PathVariable(value = "urlPart", required = false) String urlPart) {
        return new ResponseEntity<>(serviceUrlService.getAllUrlsContainingString(urlPart), HttpStatus.OK);
    }

    @GetMapping("/api/service/{id}")
    public ResponseEntity<ServiceUrlDto> getServiceUrl(@PathVariable("id") Long id) {
        return new ResponseEntity<>(serviceUrlService.getServiceById(id), HttpStatus.OK);
    }

    @PostMapping("/api/service/")
    public ResponseEntity<ServiceUrlDto> addService(@RequestBody ServiceUrlDto serviceUrlDto) {
        return new ResponseEntity<>(serviceUrlService.add(serviceUrlDto), HttpStatus.CREATED);
    }

    @PutMapping("/api/service/")
    public ResponseEntity<ServiceUrlDto> editService(@RequestBody ServiceUrlDto serviceUrlDto) {
        // return new ResponseEntity<>(serviceUrlService.addByHyper(serviceUrlDto), HttpStatus.CREATED);
        return new ResponseEntity<>(serviceUrlService.edit(serviceUrlDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/api/service/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteServiceUrl(@PathVariable("id") Long id) {
        serviceUrlService.delete(id);
    }

}
