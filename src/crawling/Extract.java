package crawling;

import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLDocument;
import de.l3s.boilerpipe.sax.HTMLFetcher;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Extract {

    public void extractLinks() throws Exception{
        Scanner scan = new Scanner(new File("linksInfo.json"));
        while (scan.hasNext()) {
            String linkIdPairs[] = scan.nextLine().split(":", 2);
            int docID = Integer.parseInt(linkIdPairs[0]);
            String url = linkIdPairs[1];
            // obtain page info and write data to file
            obtainPageInfo(url,docID);
        }
    }

    public void obtainPageInfo(String url, int docID)  throws Exception {
            System.out.println(url);
           try {
               final HTMLDocument htmlDoc = HTMLFetcher.fetch(new URL(url));
               final TextDocument doc = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();
               // extract text from html document
               String content = ArticleExtractor.INSTANCE.getText(doc);
               // write to file html
               PrintWriter out = new PrintWriter("Docs\\" + docID + ".html");
               out.println("<base href=\"" + url + "\" >");
               out.println("<meta DOCID:" + docID + " http-equiv=\"Content-Type\" content=\"text-html; charset=utf-8\" />");
               out.println("<title>" + doc.getTitle() + "</title>");
               out.println("<body>" + content + "</body>");
               out.close();
           }
           catch (Exception e) {

           }
    }
}