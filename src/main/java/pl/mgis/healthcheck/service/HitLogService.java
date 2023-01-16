package pl.mgis.healthcheck.service;

import org.springframework.stereotype.Service;
import pl.mgis.healthcheck.dto.HitLogDto;
import pl.mgis.healthcheck.dto.Mapper;
import pl.mgis.healthcheck.model.HitLog;
import pl.mgis.healthcheck.repository.HitLogRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HitLogService {

    private final HitLogRepository hitLogRepository;

    public HitLogService(HitLogRepository hitLogRepository) {
        this.hitLogRepository = hitLogRepository;
    }

    public List<HitLog> findTodayHitLog(){
        return hitLogRepository.findHitLogFromPresentDay();
    }

    public List<HitLogDto> findTodayHitLogDto(){
        return hitLogRepository.findHitLogFromPresentDay()
                .stream().map(Mapper::hitLogToDto).collect(Collectors.toList());
    }

    public HitLog save(HitLog hitLog){
        return hitLogRepository.save(hitLog);
    }

    public List<HitLogDto> findAll() {
        return hitLogRepository.findAll().stream().map(Mapper::hitLogToDto).collect(Collectors.toList());
    }

    public List<HitLogDto> findHitLogFromDay(String fromDay) {
        return hitLogRepository.findAllFromDay(fromDay).stream().map(Mapper::hitLogToDto).collect(Collectors.toList());
    }

    public void deleteAllHitLogs() {
        hitLogRepository.deleteAll();
    }
}
