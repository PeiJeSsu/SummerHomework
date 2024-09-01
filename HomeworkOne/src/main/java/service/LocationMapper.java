package service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class LocationMapper {
    private static final int TIMEOUT = 30000; // 设置超时时间为 30 秒（30000 毫秒）

    public static void main(String[] args) {
        LocationMapper mapper = new LocationMapper();
        mapper.fetchLinkTitleByTitle("七堵區"); // 处理基隆市的初始页面
    }

    // 从基隆市的初始页面获取所有子页面的链接并处理
    public void fetchLinkTitleByTitle(String linkText) {
        try {
            // 1. 连接到基隆市的初始网页并获取文档对象，设置超时时间
            Document document = Jsoup.connect("https://www.travelking.com.tw/tourguide/taiwan/keelungcity/")
                    .timeout(TIMEOUT)
                    .get();

            // 2. 使用 id 选择器选择特定的 <div> 元素，然后找到所有 <a> 元素
            Element divElement = document.getElementById("guide-point");
            if (divElement != null) {
                // 打印所有 <h4> 元素的文本，调试信息
                Elements h4Elements = divElement.select("h4");
                for (Element h4 : h4Elements) {
                    System.out.println("Found <h4>: " + h4.text());
                }

                // 通过标题文本获取 <h4> 元素，并查找下一个 <ul> 元素中的链接
                Element headerElement = divElement.select("h4:contains(" + linkText + ")").first();
                if (headerElement != null) {
                    // 找到 <h4> 元素之后的第一个 <ul> 元素
                    Element ulElement = headerElement.nextElementSibling();
                    if (ulElement != null) {
                        Elements linkElements = ulElement.select("a"); // 获取所有 <a> 元素

                        // 4. 对每个链接页面，获取页面标题
                        for (Element linkElement : linkElements) {
                            String url = linkElement.attr("href");
                            String absoluteUrl = "https://www.travelking.com.tw" + url; // 构建绝对 URL

                            // 连接到上述 URL 并获取该页面的标题，设置超时时间
                            fetchPageTitle(absoluteUrl, linkText);
                        }
                    } else {
                        System.out.println("No <ul> element found after <h4> with the specified text.");
                    }
                } else {
                    System.out.println("No <h4> element found with the specified text.");
                }
            } else {
                System.out.println("No <div> element found with the specified id.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 从给定 URL 获取页面标题
    public void fetchPageTitle(String pageUrl, String linkText) {
        try {
            Document document = Jsoup.connect(pageUrl)
                    .timeout(TIMEOUT)
                    .get();
            // 选择 meta 标签，并提取 content 属性的值
            String nameElement = document.selectFirst("meta[itemprop='name']").attr("content");
            String imageElement = document.selectFirst("meta[itemprop='image']").attr("content");
            String descriptionElement = document.selectFirst("meta[itemprop='description']").attr("content");
            String addressElement = document.selectFirst("meta[itemprop='address']").attr("content");
            String strongElement = document.selectFirst("span.point_pc + span strong").text();


            System.out.println(nameElement + "\n" + linkText + "\n" + strongElement + "\n" + imageElement + "\n" + descriptionElement + "\n" + addressElement + "\n");
        } catch (IOException e) {
            System.out.println("Error fetching the title for URL: " + pageUrl);
            e.printStackTrace();
        }
    }

}
