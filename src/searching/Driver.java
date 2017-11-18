package searching;

import java.io.IOException;
import java.util.Scanner;

public class Driver {

    public static void main(String[] args) throws Exception {
        try {
            SearchEngine searchEngine = SearchEngine.getInstance();
            String s;
            boolean keepAlive = true;
            while(keepAlive) {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Please enter your query:");
                s = scanner.nextLine();
                searchEngine.search(s);
                System.out.println("Search another one? Y/N");
                s = scanner.nextLine();
                keepAlive = s.equalsIgnoreCase("y");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            System.out.println("Thank you, bye!");
        }
    }
}
