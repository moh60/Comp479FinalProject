package indexer;

import java.nio.file.Path;
import java.nio.file.Paths;

public class indexDriver {

    public static void main (String [] args){

        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path tokenStreamPath = Paths.get(currentPath.toString(), "token_stream", "token_stream.bin");
        Path invertedIndexPath = Paths.get(currentPath.toString(), "index");

        InvertedIndexer indexer = new InvertedIndexer(tokenStreamPath, invertedIndexPath);
        indexer.constructIndex(500);

    }

}
