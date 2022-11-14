package pl.mgis.restapi.service;

import org.springframework.stereotype.Service;
import pl.mgis.restapi.model.HitLog;
import pl.mgis.restapi.repository.HitLogRepository;

@Service
public class HitLogService {

    private final HitLogRepository hitLogRepository;

    public HitLogService(HitLogRepository hitLogRepository) {
        this.hitLogRepository = hitLogRepository;
    }

    public HitLog save(HitLog hitLog){
        return hitLogRepository.save(hitLog);
    }
}
