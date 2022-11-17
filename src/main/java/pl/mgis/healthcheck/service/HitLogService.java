package pl.mgis.healthcheck.service;

import org.springframework.stereotype.Service;
import pl.mgis.healthcheck.model.HitLog;
import pl.mgis.healthcheck.repository.HitLogRepository;

import java.util.List;

@Service
public class HitLogService {

    private final HitLogRepository hitLogRepository;

    public HitLogService(HitLogRepository hitLogRepository) {
        this.hitLogRepository = hitLogRepository;
    }

    public HitLog save(HitLog hitLog){
        return hitLogRepository.save(hitLog);
    }

    public List<HitLog> findTodayHitLog(){
        return hitLogRepository.findHitLogsFromPresentDayLessThenFour();
    }
}
