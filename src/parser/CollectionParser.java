package parser;

import javafx.util.Pair;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class CollectionParser {

    private int docCount = 0;
    private int tokenCount = 0;
    private HashMap<String, Boolean> settings;

    public CollectionParser(HashMap<String, Boolean> settings) {
        this.settings = settings;
    }

    public int getDocCount() {
        return docCount;
    }

    public void setDocCount(int docCount) {
        this.docCount = docCount;
    }

    public int getTokenCount() {
        return tokenCount;
    }

    public void setTokenCount(int tokenCount) {
        this.tokenCount = tokenCount;
    }

    public HashMap<String, Boolean> getSettings() {
        return settings;
    }

    public void setSettings(HashMap<String, Boolean> settings) {
        this.settings = settings;
    }

    public String parse_collection(Path tokenStreamPath, Path collectionDir, int noDocs) {

        // Saves map of docid:list of tokens
        HashMap<Integer, ArrayList<String>> docsTokens = new HashMap<>();

        // Open each document and parse it to get tokens
        for (int i = 0; i < noDocs; i++) {

            File filePath = new File(String.format(Paths.get(collectionDir.toString(), "/%d.html").toString(), i));

            Document doc = null;
            try {
                doc = Jsoup.parse(filePath, "UTF-8", "");
            } catch (IOException e) {
                System.out.println("Unable to read doc no:" + i);
                continue;
            }

            docsTokens.put(i, DocParser.parse_doc(doc, this.settings));

        }

        // FOR debugging: save the hashmap as a JSON file
        try {
            try {
                PrintWriter fileWriter = new PrintWriter(Paths.get(tokenStreamPath.toString(), "doc_tokens.json").toString(), "UTF-8");
                fileWriter.write(JSONObject.toJSONString(docsTokens));
                fileWriter.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        // Generate the token stream based on the docID: list of tokens map
        generateTokenStream(tokenStreamPath, docsTokens);

        return "";

    }

    private void generateTokenStream(Path tokenStreamPath, HashMap<Integer, ArrayList<String>> docsTokens){

        // Make list of pairs<docId, term>
        ArrayList<Pair<Integer,String>> tokenStreamList = new ArrayList<>();
        for (Integer docId: docsTokens.keySet()){
            for (String token: docsTokens.get(docId)){
                tokenStreamList.add(new Pair<>(docId, token));
            }
        }

        // Serialize and save list
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(Paths.get(tokenStreamPath.toString(), "token_stream.bin").toString());
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(tokenStreamList);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
