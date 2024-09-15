package homeworkfour.contorller;

import homeworkfour.model.Sight;
import homeworkfour.service.LocationMapper;
import homeworkfour.service.LocationSelector;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MainController {

    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private LocationSelector locationSelector;

    @PostConstruct
    public void fetchAndStoreData() {
        locationMapper.fetchAndStoreAllAttractions();
    }

    @GetMapping("/SightAPI")
    public List<Sight> findBySectionName(@RequestParam String zone) {
        return locationSelector.findBySectionName(zone);
    }
}
