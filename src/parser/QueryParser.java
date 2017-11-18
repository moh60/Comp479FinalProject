package parser;

import java.util.ArrayList;

public class QueryParser extends DocParser {

    private static ArrayList<String> tokenizeQuery(String unprocessedText){
        return tokenizeText(unprocessedText);
    }

    public static ArrayList<String> preprocessQuery(String unprocessedText){
        return preprocessTokens(tokenizeQuery(unprocessedText), SettingParser.readSettings());
    }


}
