package homeworkfour.service;

import homeworkfour.model.Sight;
import homeworkfour.repository.SightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationSelector {

    @Autowired
    private SightRepository sightRepository;

    public List<Sight> findBySectionName(String zone) {
        return sightRepository.findByZone(zone);
    }
}
