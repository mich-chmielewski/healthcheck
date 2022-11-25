package pl.mgis.healthcheck.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.mgis.healthcheck.dto.HitLogDto;
import pl.mgis.healthcheck.service.HitLogService;

import java.util.List;

@RestController
public class HitLogRestController {

    private final HitLogService hitLogService;


    public HitLogRestController(HitLogService hitLogService) {
        this.hitLogService = hitLogService;
    }

    @GetMapping("/api/hitlogs/")
    public ResponseEntity<List<HitLogDto>> getAll() {
        return new ResponseEntity<>(hitLogService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/api/hitlogs/today")
    public ResponseEntity<List<HitLogDto>> getAllFromToday() {
        return new ResponseEntity<>(hitLogService.findTodayHitLogDto(), HttpStatus.OK);
    }

    @DeleteMapping("/api/hitlogs/")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteAll() {
        hitLogService.deleteAllHitLogs();
    }


}
