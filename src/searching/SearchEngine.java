package searching;

import indexer.InvertedIndexer;
import javafx.util.Pair;
import parser.DocSentimentScore;
import parser.QueryParser;
import parser.extractAfinn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SearchEngine {

    private final static Path currentPath = Paths.get(System.getProperty("user.dir"));
    private static Map<String, Integer> aFinnPair = new HashMap<>();
    private TreeMap<Integer, Integer> docSentimentScore;
    private TreeMap<Pair<String, Integer>, TreeMap<Integer, Integer>> invertedIndex;
    private TreeMap<Integer, String> docURL;
    private static SearchEngine searchEngine;
    private static int queryScore = 0;

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
        loadDocURL();
    }

    @SuppressWarnings("unchecked")
    public void search(String query){
        String[] queryTokens = QueryParser.preprocessQuery(query);

        //Process query to get all query-sentimentScore pair.
        List<Pair<String, Integer>> queryToken_sentiment_pairs = processQuery(queryTokens);

        TreeSet<Integer> unrankedResult = fetchFromIndex(queryToken_sentiment_pairs);

        System.out.println("Query sentiment score: "+ queryScore);

        rank(queryScore, unrankedResult);

        //reset query score
        queryScore = 0;
    }

    @SuppressWarnings("unchecked")
    private void rank(int querySentimentScore, TreeSet<Integer> unrankedResult) {
        Pair<Integer, Integer>[] resultArray;
        int i = 0;

        if(unrankedResult !=null && !unrankedResult.isEmpty()){
            resultArray = new Pair[unrankedResult.size()];

            //Fetch all id-sentimentScore pair from DocSentimentScore Map.
            while(!unrankedResult.isEmpty()){

                int key = unrankedResult.pollFirst();
                Pair<Integer, Integer> pair = new Pair<>(key, this.docSentimentScore.get(key));
                resultArray[i] = pair;
                i++;
            }

            //Sort by all results' sentiment score in descending order.
            Arrays.sort(resultArray, Comparator.comparing(Pair::getValue));

            if(querySentimentScore < 0){
                System.out.println("Result:");

                for(Pair<Integer, Integer> result: resultArray) {
                    System.out.println("Doc id: " + result.getKey() + ", URL: " + this.docURL.get(result.getKey())
                            + ", Sentiment score: " + result.getValue());

                }

            }else{
                System.out.println("Result:");

                for(int j = resultArray.length-1; j >= 0; j--) {
                    System.out.println("Doc id: " + resultArray[j].getKey() + ", URL: " + this.docURL.get(resultArray[j].getKey())
                            + ", Sentiment score: " + resultArray[j].getValue());
                }
            }
        }else {

            System.out.println("Result Not Found.");
        }
    }

    private int getDocumentFrequency(TreeMap<Integer, Integer> postingsList){
        return postingsList.size();
    }

    private TreeSet<Integer> fetchFromIndex(List<Pair<String, Integer>> queryToken_sentiment_pairs) {
        //Fetch all docIDs for given query.
        if(queryToken_sentiment_pairs != null && this.invertedIndex != null && !this.invertedIndex.isEmpty()){
            TreeSet<Integer> result = new TreeSet<>();
            for(Pair<String, Integer> key: queryToken_sentiment_pairs){
                if(this.invertedIndex.containsKey(key)){

                    TreeMap<Integer, Integer> value = invertedIndex.get(key);
                    Set<Integer> keySet = value.keySet();

                    if(result.size() == 0){
                            result.addAll(keySet);

                    }else{
                        union(result, keySet);
                    }
                }
            }

            return result;
        }
        return null;
    }

    private static List<Pair<String,Integer>> processQuery(String[] queryTokens) {
        if(queryTokens != null && queryTokens.length >0) {
            List<Pair<String, Integer>> result = new ArrayList<>();

            //Initialize 'term-sentimentScore' key pairs for Inverted Index.
            for (String queryTerm : queryTokens) {
                if (aFinnPair != null && !aFinnPair.isEmpty()) {
                    Pair<String, Integer> keyPair;
                    if (aFinnPair.containsKey(queryTerm)) {
                        keyPair = new Pair<>(queryTerm, aFinnPair.get(queryTerm));
                        queryScore += aFinnPair.get(queryTerm);
                    } else {
                        keyPair = new Pair<>(queryTerm, 0);
                    }
                    result.add(keyPair);
                }
            }
            return result;
        }
        return null;
    }

    private static void union(Set<Integer> postingList1, Set<Integer> postingList2) {

        //Union of two posting list.
        if(postingList2 != null) {

            postingList1.addAll(postingList2);

        }
    }

    private void loadDocuments() throws IOException {
        aFinnPair = extractAfinn.unserailizeAffin();
        this.docSentimentScore = DocSentimentScore.readDocSentimentScore();
    }

    private void loadIndex() {
        Path invertedIndexPath = Paths.get(currentPath.toString(), "index");
        InvertedIndexer invertedIndexer = new InvertedIndexer(null, invertedIndexPath);
        this.invertedIndex = invertedIndexer.loadIndex();
    }

    private void loadDocURL() throws FileNotFoundException{
        this.docURL = new TreeMap<>();
        Scanner scan;
        scan = new Scanner(new File("linksInfo.json"));
        while (scan.hasNext()) {
            String linkIdPairs[] = scan.nextLine().split(":", 2);
            int id = Integer.parseInt(linkIdPairs[0]);
            this.docURL.put(id, linkIdPairs[1]);
        }
    }

}
