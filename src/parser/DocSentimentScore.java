package parser;

import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class DocSentimentScore {

    public static void unserialiazeTokenStream() throws Exception {

        // load afinn map
        HashMap<String, Integer> afinnTermScores = extractAfinn.unserailizeAffin();

        //deserialize the token stream
        InputStream file = new FileInputStream("token_stream\\token_stream.txt");
        InputStream buffer = new BufferedInputStream(file);
        ObjectInput input = new ObjectInputStream(buffer);

        // add token stream into an arrayList
        ArrayList<Pair<Integer, String>> tokenStreamCollection = (ArrayList<Pair<Integer, String>>) input.readObject();

        // stores document sentiment score in a TreeMap
        TreeMap<Integer, Integer> documentSentimentScore = new TreeMap<>();

        // for each document id, sum its sentiment score
        for (Pair<Integer, String> tokenStream : tokenStreamCollection) {
            // if afinn document has term
            if (afinnTermScores.containsKey(tokenStream.getValue())) {
                // if new document assign default score of 0
                if (!documentSentimentScore.containsKey(tokenStream.getKey())) {
                    documentSentimentScore.put(tokenStream.getKey(), 0);
                }
                // if existing document then add to existing document sentiment score
                else {
                    int currentDocumentSentimentScore = documentSentimentScore.get(tokenStream.getKey());
                    int termScore = afinnTermScores.get(tokenStream.getValue());
                    int newScore = currentDocumentSentimentScore + termScore;
                    documentSentimentScore.put(tokenStream.getKey(), newScore);
                }
            }
            // term does not exist in afinn
            else {
                // if new document, assign default score of 0
                if (!documentSentimentScore.containsKey(tokenStream.getKey())) {
                    documentSentimentScore.put(tokenStream.getKey(), 0);
                }
                // if existing document then add to existing document sentiment score
                else {
                    int currentDocumentSentimentScore = documentSentimentScore.get(tokenStream.getKey());
                    int termScore = 0;
                    int newScore = currentDocumentSentimentScore + termScore;
                    documentSentimentScore.put(tokenStream.getKey(), newScore);
                }
            }
        }
        writeDocSentimentScoreToFile(documentSentimentScore);
    }
    public static void writeDocSentimentScoreToFile(TreeMap<Integer,Integer> docSentimentScore) throws IOException {
        // create a file to store doc sentiment data
        PrintWriter out = new PrintWriter("res\\docSentimentScore.txt");
        out.println(docSentimentScore);
        out.close();
    }
}

