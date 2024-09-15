package homeworkfour.contorller;

import homeworkfour.model.Sight;
import homeworkfour.service.LocationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MainController {

    @Autowired
    private LocationMapper locationMapper;

    @GetMapping("/SightAPI")
    public List<Sight> findAttractions(@RequestParam String zone) {
        return locationMapper.fetchSightsByTitle(zone + "區");
    }
}
