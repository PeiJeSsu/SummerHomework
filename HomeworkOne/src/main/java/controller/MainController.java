package controller;

import model.Sight;
import service.LocationMapper;

import java.util.List;

public class MainController {
    public List<Sight> findAttractions(String location) {
        LocationMapper mapper = new LocationMapper();
        List<Sight> sights = mapper.fetchSightsByTitle(location + "區");

        return sights;
    }
}
