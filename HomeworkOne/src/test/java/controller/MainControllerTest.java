package controller;

import model.Sight;
import java.util.List;

public class MainControllerTest {
    public static void main(String[] args) {
        String[] locations = {"七堵", "中山", "中正", "仁愛", "安樂", "信義", "暖暖"};

        MainController locationController = new MainController();

        for (String location : locations) {
            List<Sight> sights = locationController.findAttractions(location);

            System.out.println("Attractions in " + location + ":");
            for (Sight sight : sights) {
                System.out.println(sight);
            }

            System.out.println();
        }
    }
}
