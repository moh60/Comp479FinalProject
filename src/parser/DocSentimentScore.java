package parser;

import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DocSentimentScore {

    public static void unserialiazeTokenStream() throws Exception {
        //deserialize the quarks.ser file
        InputStream file = new FileInputStream("token_stream\\token_stream.txt");
        InputStream buffer = new BufferedInputStream(file);
        ObjectInput input = new ObjectInputStream (buffer);
        //deserialize the ArrayList
        ArrayList<Pair<Integer,String>> tokenStreamCollection = (ArrayList<Pair<Integer,String>>)input.readObject();
        // for doc id get
        for(Pair<Integer,String> tokenStream: tokenStreamCollection){
            System.out.println(tokenStream);
        }
    }


    public static void main(String[] args) throws Exception {
        unserialiazeTokenStream();
    }
}

