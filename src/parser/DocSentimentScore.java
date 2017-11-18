package parser;

import javafx.util.Pair;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class DocSentimentScore {

    @SuppressWarnings("unchecked")
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
        saveDocSentimentScore(documentSentimentScore);
    }
    private static void writeDocSentimentScoreToFile(TreeMap<Integer,Integer> docSentimentScore) throws IOException {
        // create a file to store doc sentiment data
        PrintWriter out = new PrintWriter("res\\docSentimentScore.txt");
        out.println(docSentimentScore);
        out.close();
    }

    private static void saveDocSentimentScore(TreeMap<Integer,Integer> docSentimentScore) {
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path docSentimentScorePath = Paths.get(currentPath.toString(), "res");

        FileOutputStream fout;
        try {
            fout = new FileOutputStream(Paths.get(docSentimentScorePath.toString(), "doc_sentiment.bin").toString(),
                    false);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(docSentimentScore);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("unchecked")
    public static TreeMap<Integer, Integer> readDocSentimentScore(){
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path docSentimentScoreBin = Paths.get(currentPath.toString(), "res", "doc_sentiment.bin");
        TreeMap<Integer, Integer> docSentScore = null;

        File file = new File(docSentimentScoreBin.toString());
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            docSentScore = (TreeMap<Integer, Integer>) objectInputStream.readObject();

            objectInputStream.close();
            fileInputStream.close();
        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return docSentScore;
    }
}

