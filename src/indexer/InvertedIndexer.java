package indexer;

import javafx.util.Pair;
import org.json.simple.JSONObject;
import parser.extractAfinn;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

public class InvertedIndexer {

    private Path tokenStreamPath;
    private Path invertedIndexPath;
    private HashMap<String, Integer> aFinnScores;
    private ArrayList<Pair<Integer, String>> tokenStream;

    public InvertedIndexer(Path tokenStreamPath, Path invertedIndexPath) {
        this.tokenStreamPath = tokenStreamPath;
        this.invertedIndexPath = invertedIndexPath;
    }


    public void constructIndex(int blockSize) {

        this.loadTokenStream();
        this.loadaFinnScores();

        this.spimiInvert(blockSize);
        this.createIndex();

    }

    public void loadTokenStream() {

        // Serialize and save list
        FileInputStream fout;
        try {
            fout = new FileInputStream(tokenStreamPath.toString());
            ObjectInputStream inputStream = new ObjectInputStream(fout);
            tokenStream = (ArrayList<Pair<Integer, String>>) inputStream.readObject();
            inputStream.close();
            fout.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void loadaFinnScores() {
        try {
            aFinnScores = extractAfinn.unserailizeAffin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public TreeMap<Pair<String, Integer>, TreeMap<Integer, Integer>> loadIndex(){
        TreeMap<Pair<String, Integer>, TreeMap<Integer, Integer>> returnObj = null;
        Path objectPath = Paths.get(this.invertedIndexPath.toString(), "inverted_index.bin");
        File indexFile = new File(objectPath.toString());

        FileInputStream fileInputStream;

        try {
            fileInputStream = new FileInputStream(indexFile);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            returnObj = (TreeMap<Pair<String, Integer>, TreeMap<Integer, Integer>>)objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return returnObj;
    }

    public void spimiInvert(int blockSize) {

        this.cleanBlockDir();

        int blockNo = 0;
        // Dictionary for block
        TreeMap<Pair<String, Integer>, TreeMap<Integer, Integer>> tmpBlockDict;

        Iterator<Pair<Integer, String>> tokenStreamIterator = tokenStream.iterator();

        while (tokenStreamIterator.hasNext()) {

            tmpBlockDict = new TreeMap<>(new TreeComparator());

            while (tmpBlockDict.size() <= blockSize && tokenStreamIterator.hasNext()) {

                // Pair from token stream
                Pair<Integer, String> tokenPair = tokenStreamIterator.next();

                // Get the term from the block's dictionary
                Pair<String, Integer> termPair = null;
                for (Pair<String, Integer> pair : tmpBlockDict.keySet()) {
                    if (pair.getKey().equals(tokenPair.getValue())) {
                        termPair = pair;
                        break;
                    }
                }

                // Get Postings list or create if not present
                TreeMap<Integer, Integer> postingsList;
                if (termPair != null) {
                    postingsList = tmpBlockDict.get(termPair);
                } else {
                    postingsList = new TreeMap<>();
                }

                // Increase the term frequency if already in map or
                int docID = tokenPair.getKey();
                if (postingsList.containsKey(docID)) {
                    postingsList.replace(docID, postingsList.get(docID) + 1);
                } else {
                    postingsList.put(docID, 1);
                }

                // Save term and postings list
                if (termPair != null) {
                    tmpBlockDict.replace(termPair, postingsList);
                } else {
                    tmpBlockDict.put(new Pair<>(tokenPair.getValue(),
                            aFinnScores.getOrDefault(tokenPair.getValue(), 0)), postingsList);
                }

            }

            saveBlock(tmpBlockDict, blockNo);

            blockNo++;

        }

    }

    public void createIndex() {

        // Get all blocks
        File directory = new File(Paths.get(invertedIndexPath.toString(), "blocks").toString());

        TreeMap<Pair<String, Integer>, TreeMap<Integer, Integer>> invertedIndex = new TreeMap<>(new TreeComparator());

        for (File block : directory.listFiles()) {

            // Don't read placeholder file
            if (block.getName().equals(".placeholder")) {
                continue;
            }

            TreeMap<Pair<String, Integer>, TreeMap<Integer, Integer>> blockDict = loadBlock(block);

            for (Pair<String, Integer> termPair : blockDict.keySet()) {

                // Merge the blocks
                if (invertedIndex.containsKey(termPair)) {

                    blockDict.get(termPair).forEach((docIdBlk, termFreqBlk) ->
                            invertedIndex.get(termPair).merge(docIdBlk, termFreqBlk, (docId, termFrq) -> docId + termFrq));

                } else {
                    invertedIndex.put(termPair, blockDict.get(termPair));
                }

            }

        }

//        System.out.println(invertedIndex);
        this.cleanBlockDir();
        this.saveInvertedIndex(invertedIndex);

        // For debugging save index as JSON
        this.saveJsonIndex(invertedIndex);

    }

    public TreeMap<Pair<String, Integer>, TreeMap<Integer, Integer>> loadBlock(File blockFile) {

        FileInputStream fin;
        try {
            fin = new FileInputStream(blockFile);
            ObjectInputStream objectInputStream = new ObjectInputStream(fin);
            TreeMap<Pair<String, Integer>, TreeMap<Integer, Integer>> returnedObj =
                    (TreeMap<Pair<String, Integer>, TreeMap<Integer, Integer>>) objectInputStream.readObject();
            objectInputStream.close();
            fin.close();
            return returnedObj;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;

    }

    public void saveBlock(TreeMap<Pair<String, Integer>, TreeMap<Integer, Integer>> blockDict, int blockNo) {

        FileOutputStream fout;
        try {
            fout = new FileOutputStream(Paths.get(invertedIndexPath.toString(), "blocks", String.format("block%d.bin", blockNo)).toString(),
                    false);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(blockDict);
            oos.close();
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void cleanBlockDir() {

        File directory = new File(Paths.get(invertedIndexPath.toString(), "blocks").toString());

        // Get all files in directory
        for (File file : directory.listFiles()) {

            if (file.getName().equals(".placeholder")) {
                continue;
            }

            try {
                // Delete each file
                if (!Files.deleteIfExists(file.toPath())) {
                    // Failed to delete file
                    System.out.println("Failed to delete " + file);
                }
            } catch (IOException ignored) {
            }
        }
    }

    public void saveInvertedIndex(TreeMap index) {

        FileOutputStream fout;
        try {
            fout = new FileOutputStream(Paths.get(invertedIndexPath.toString(), "inverted_index.bin").toString(),
                    false);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(index);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void saveJsonIndex(TreeMap<Pair<String, Integer>, TreeMap<Integer, Integer>> invertedIndex) {

        try {
            PrintWriter fileWriter = new PrintWriter(Paths.get(invertedIndexPath.toString(),
                    "inverted_index.json").toString(), "UTF-8");
            fileWriter.write(JSONObject.toJSONString(invertedIndex));
            fileWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
