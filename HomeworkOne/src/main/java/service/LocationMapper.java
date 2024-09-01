package service;

import model.Sight;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LocationMapper {
    private static final int TIMEOUT = 30000;
    private static final String BASE_URL = "https://www.travelking.com.tw";
    private static final String GUIDE_POINT_ID = "guide-point";
    private static final String TOUR_GUIDE_PATH = "/tourguide/taiwan/keelungcity/";
    private static final Logger LOGGER = Logger.getLogger(LocationMapper.class.getName());

    public List<Sight> fetchSightsByTitle(String linkText) {
        List<Sight> sights = new ArrayList<>();

        try {
            Document document = fetchDocument(BASE_URL + TOUR_GUIDE_PATH);

            Element guidePointDiv = document.getElementById(GUIDE_POINT_ID);
            if (guidePointDiv == null) {
                LOGGER.warning("No <div> element found with id: " + GUIDE_POINT_ID);
                return sights;
            }

            Element headerElement = findHeaderElement(guidePointDiv, linkText);
            if (headerElement == null) {
                LOGGER.warning("No <h4> element found with the specified text: " + linkText);
                return sights;
            }

            Element ulElement = headerElement.nextElementSibling();
            if (ulElement == null) {
                LOGGER.warning("No <ul> element found after <h4> with the specified text.");
                return sights;
            }

            sights.addAll(processLinks(ulElement, linkText));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error fetching the page", e);
        }

        return sights;
    }

    private Document fetchDocument(String url) throws IOException {
        return Jsoup.connect(url).timeout(TIMEOUT).get();
    }

    private Element findHeaderElement(Element divElement, String linkText) {
        return divElement.select("h4:contains(" + linkText + ")").first();
    }

    private List<Sight> processLinks(Element ulElement, String linkText) {
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

        return sights;
    }

    private Sight fetchPageDetails(String pageUrl, String linkText) {
        Sight sight = new Sight();

        try {
            Document document = fetchDocument(pageUrl);
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
}