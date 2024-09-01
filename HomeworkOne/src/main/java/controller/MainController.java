package controller;

import model.Sight;
import service.LocationMapper;

import java.util.List;

public class MainController {
    public static void main(String[] args) {
        LocationMapper mapper = new LocationMapper();
        List<Sight> sights = mapper.fetchSightsByTitle("暖暖區");

        // 打印所有 Sight 对象
        sights.forEach(sight -> {
            System.out.println(sight);
            System.out.println();
        });
    }
}
