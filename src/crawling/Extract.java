package crawling;

import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLDocument;
import de.l3s.boilerpipe.sax.HTMLFetcher;
import java.io.PrintWriter;
import java.net.URL;

public class Extract {

    public void obtainPageInfo(String url)  throws Exception {
            final HTMLDocument htmlDoc = HTMLFetcher.fetch(new URL(url));
            final TextDocument doc = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();
            // extract text from html document
            String content = ArticleExtractor.INSTANCE.getText(doc);
            // write to file html
            PrintWriter out = new PrintWriter ("test.html");
            out.println("<base href=\"" + url + "\" >");
            out.println("<meta http-equiv=\"Content-Type\" content=\"text-html; charset=utf-8\" />");
            out.println("<title>" + doc.getTitle() + "</title>");
            out.println("<body>" + content + "</body>");
            out.close();
    }
    public static void main(String[] args) throws Exception {
        Extract url = new Extract();
        url.obtainPageInfo("https://csu.qc.ca/content/student-groups-associations"); //test
    }
}