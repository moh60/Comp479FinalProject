package crawling;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

// extract links
public class Crawler {
    public static void main(String[] args) throws IOException {
        String url = "https://csu.qc.ca/content/student-groups-associations"; // test url
        System.out.println("Fetching: " + url);
        Document doc = Jsoup.connect(url).get();         // getting sub links
        Elements links = doc.select("a[href]");
        // go through list of link elements
        for (Element link : links) {
            String linkAttr = link.attr("abs:href");
            if (!linkAttr.contains("mailto:")) {
                System.out.println(link.attr("abs:href"));
            }
        }
    }
}