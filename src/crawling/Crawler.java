package crawling;

import javafx.print.Printer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.util.*;

public class Crawler {
    // crawl links
    public void crawl(String url, int upperBound) throws Exception {
        int depth = 0;
        System.out.println("Fetching: " + url);
        // getting sub links
        Document doc = Jsoup.connect(url).get();
        Elements links = doc.select("a[href]");
        // load links from file
        TreeMap<Integer,String> linkSet = new TreeMap<>();
        Scanner scan = new Scanner(new File("linksInfo.json"));
        while (scan.hasNext()) {
            String linkIdPairs[] = scan.nextLine().split(":", 2);
            int id = Integer.parseInt(linkIdPairs[0]);
            linkSet.put(id, linkIdPairs[1]);
        }
        // set doc ID
        int docID;
        if ( linkSet.isEmpty()) {
            docID = 0;
        }
        else {
            docID = linkSet.lastKey()+1;
        }
        // check if we have exceeded upper bound limit
        if (!(docID > upperBound)) {
            // get robot links
            List<String> robotLinks = getRobots(url);
            // go through list of link elements
            for (Element link : links) {
                String linkAttr = link.attr("abs:href");
                if (!linkAttr.equals("")) {
                    URL urlObject = new URL(linkAttr);
                    System.out.println(urlObject);
                    if (!linkAttr.contains("mailto:") && linkAttr.contains("http") && !robotLinks.contains(urlObject.getPath())) {
                        if (!linkSet.containsValue(link.attr("abs:href")) || linkSet.isEmpty()) {
                            linkSet.put(docID, link.attr("abs:href"));
                            writeToFile(docID, link);
                            docID++;
                        }
                    }
                }
            }
        }
    }

    // write links to file
    public void writeToFile(int docID, Element link) throws IOException {
        FileWriter fw = new FileWriter("linksInfo.json", true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter out = new PrintWriter(bw);
        out.println(docID + ":" + link.attr("abs:href"));
        out.close();
    }

    // get disallowed robot links
    public List getRobots(String url) throws Exception {
        URL link = new URL(url);
        String host = link.getHost();
        try(BufferedReader in = new BufferedReader(new InputStreamReader(new URL("https://" + host + "/robots.txt").openStream()))) {
            String line = null;
            List<String> robotLinks = new ArrayList<>();
            while((line = in.readLine()) != null) {
                if (line.contains("Disallow")) {
                    String disallowedLinks[] = line.split(" ");
                    robotLinks.add(disallowedLinks[1]);
                }
            }
            return robotLinks;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}