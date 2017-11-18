package searching;

import indexer.InvertedIndexer;
import javafx.util.Pair;
import parser.DocSentimentScore;
import parser.extractAfinn;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class SearchEngine {

    private final static Path currentPath = Paths.get(System.getProperty("user.dir"));
    private Map<String, Integer> aFinnPair = new HashMap<>();
    private TreeMap<Integer, Integer> docSentimentScore;
    private TreeMap<Pair<String, Integer>, TreeMap<Integer, Integer>> invertedIndex;
    private static SearchEngine searchEngine;

    private SearchEngine(){}

    public static SearchEngine getInstance() throws IOException {
        if(searchEngine == null){
            searchEngine = new SearchEngine();
            searchEngine.init();
        }
        return searchEngine;
    }

    private void init() throws IOException {
        loadIndex();
        loadDocuments();
    }

    private void loadDocuments() throws IOException {
        this.aFinnPair = extractAfinn.unserailizeAffin();
        this.docSentimentScore = DocSentimentScore.readDocSentimentScore();
    }

    private void loadIndex() {
        Path invertedIndexPath = Paths.get(currentPath.toString(), "index");
        InvertedIndexer invertedIndexer = new InvertedIndexer(null, invertedIndexPath);
        this.invertedIndex = invertedIndexer.loadIndex();
    }


}
