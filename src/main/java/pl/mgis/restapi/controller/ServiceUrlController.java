package pl.mgis.restapi.controller;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.mgis.restapi.dto.ServiceUrlDto;
import pl.mgis.restapi.model.ServiceUrl;
import pl.mgis.restapi.service.ServiceUrlService;

import java.util.List;
import java.util.Set;

@RestController
public class ServiceUrlController {
    private final ServiceUrlService serviceUrlService;

    public ServiceUrlController(ServiceUrlService serviceUrlService) {
        this.serviceUrlService = serviceUrlService;
    }

    @GetMapping("/api/services")
    public ResponseEntity<Set<ServiceUrlDto>> listAll() {
        return new ResponseEntity<>(serviceUrlService.getAll(), HttpStatus.OK);
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

    @PostMapping("/api/services")
    public ResponseEntity<ServiceUrlDto> addService(@RequestBody ServiceUrlDto serviceUrlDto) {
       // return new ResponseEntity<>(serviceUrlService.addByHyper(serviceUrlDto), HttpStatus.CREATED);
        return new ResponseEntity<>(serviceUrlService.add(serviceUrlDto), HttpStatus.CREATED);
    }

    @PutMapping("/api/services")
    public ResponseEntity<ServiceUrlDto> editService(@RequestBody ServiceUrlDto serviceUrlDto) {
        // return new ResponseEntity<>(serviceUrlService.addByHyper(serviceUrlDto), HttpStatus.CREATED);
        return new ResponseEntity<>(serviceUrlService.edit(serviceUrlDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/api/services/{id}")
    public void deleteServiceUrl(@PathVariable("id") Long id){
        serviceUrlService.delete(id);
    }


}
