package parser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class extractAfinn {

    public static HashMap<String, Integer> unserailizeAffin() throws IOException {
        Scanner scan = new Scanner(new File("res\\AFINN-111.txt"));
        HashMap<String, Integer> afinnPairs = new HashMap<>();
        while (scan.hasNext()) {
            String afinnTerm = scan.nextLine().replaceAll("\\s+",":");
            String pairs[] = afinnTerm.split(":");
            afinnPairs.put(pairs[0],Integer.parseInt(pairs[1]));
        }
        return afinnPairs;
    }
}
