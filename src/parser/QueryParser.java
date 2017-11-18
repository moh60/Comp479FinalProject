package parser;

import java.util.ArrayList;


public class QueryParser extends DocParser {

    private static ArrayList<String> tokenizeQuery(String unprocessedText){
        return tokenizeText(unprocessedText);
    }

    public static String[] preprocessQuery(String unprocessedText){
        if(unprocessedText != null && !unprocessedText.equals("")) {
            ArrayList<String> processedQuery = preprocessTokens(tokenizeQuery(unprocessedText), SettingParser.readSettings());
            StringBuilder stringBuilder = new StringBuilder();
            for(String s: processedQuery){
                stringBuilder.append(s);
                stringBuilder.append(" ");
            }
            String raw = stringBuilder.toString();
            return raw.split(" ");
        }
        return null;
    }
}
