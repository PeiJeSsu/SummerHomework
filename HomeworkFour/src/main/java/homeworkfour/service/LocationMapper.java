package homeworkfour.service;

import homeworkfour.model.Sight;
import homeworkfour.repository.SightRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class LocationMapper {

    private static final int TIMEOUT = 60000;
    private static final String BASE_URL = "https://www.travelking.com.tw";
    private static final String GUIDE_POINT_ID = "guide-point";
    private static final String TOUR_GUIDE_PATH = "/tourguide/taiwan/keelungcity/";
    private static final Logger LOGGER = Logger.getLogger(LocationMapper.class.getName());

    @Autowired
    private SightRepository sightRepository; // 注入 SightRepository

    public void fetchAndStoreAllAttractions() {
        sightRepository.deleteAll();

        String[] locations = {"七堵", "中山", "中正", "仁愛", "安樂", "信義", "暖暖"};

        for (String location : locations) {
            fetchSightsByTitle(location);
        }
    }

    public void fetchSightsByTitle(String linkText) {
        try {
            Document document = Jsoup.connect(BASE_URL + TOUR_GUIDE_PATH).timeout(TIMEOUT).get();



            Element guidePointDiv = document.getElementById(GUIDE_POINT_ID);
            if (guidePointDiv == null) {
                LOGGER.warning("No <div> element found with id: " + GUIDE_POINT_ID);
                return;
            }

            Element headerElement = guidePointDiv.select("h4:contains(" + linkText + ")").first();
            if (headerElement == null) {
                LOGGER.warning("No <h4> element found with the specified text: " + linkText);
                return;
            }

            Element ulElement = headerElement.nextElementSibling();
            if (ulElement == null) {
                LOGGER.warning("No <ul> element found after <h4> with the specified text.");
                return;
            }

            processLinks(ulElement, linkText);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error fetching the page", e);
        }
    }

    private void processLinks(Element ulElement, String linkText) {
        List<Sight> sights = new ArrayList<>();
        Elements linkElements = ulElement.select("a");

        for (Element linkElement : linkElements) {
            String relativeUrl = linkElement.attr("href");
            String absoluteUrl = BASE_URL + relativeUrl;
            Sight sight = fetchPageDetails(absoluteUrl, linkText);
            if (sight != null) {
                sights.add(sight);
            }
        }

        saveSights(sights);
    }

    private Sight fetchPageDetails(String pageUrl, String linkText) {
        Sight sight = new Sight();

        try {
            Document document = Jsoup.connect(pageUrl).timeout(TIMEOUT).get();;
            sight.setSightName(getMetaContent(document, "name"));
            sight.setZone(linkText);
            sight.setCategory(getCategory(document));
            sight.setPhotoURL(getMetaContent(document, "image"));
            sight.setDescription(getMetaContent(document, "description"));
            sight.setAddress(getMetaContent(document, "address"));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error fetching details for URL: " + pageUrl, e);
            return null;
        }

        return sight;
    }

    private String getMetaContent(Document document, String itemprop) {
        Element metaElement = document.selectFirst("meta[itemprop='" + itemprop + "']");
        return metaElement != null ? metaElement.attr("content") : "";
    }

    private String getCategory(Document document) {
        Element strongElement = document.selectFirst("span.point_pc + span strong");
        return strongElement != null ? strongElement.text() : "";
    }

    private void saveSights(List<Sight> sights) {
        for (Sight sight : sights) {
            try {
                sightRepository.save(sight);
                LOGGER.info("Successfully saved sight: " + sight.getSightName());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error saving sight: " + sight.getSightName(), e);
            }
        }
    }
}

