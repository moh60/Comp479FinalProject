package parser;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class DocParser {

    public static final List<String> STOPWORDSLIST = Arrays.asList("the", "of", "to", "and", "a", "in", "is", "it", "you", "that", "he",
            "was", "for", "on", "are", "with", "as", "I", "his", "they", "be", "at", "one", "have", "this", "from", "or",
            "had", "by", "hot", "but", "some", "what", "there", "we", "can", "out", "other", "were", "all", "your",
            "when", "up", "use", "word", "how", "said", "an", "each", "she", "which", "do", "their", "time", "if", "will",
            "way", "about", "many", "then", "them", "would", "write", "like", "so", "these", "her", "long", "make", "thing",
            "see", "him", "two", "has", "look", "more", "day", "could", "go", "come", "did", "my", "sound", "no", "most",
            "number", "who", "over", "know", "water", "than", "call", "first", "people", "may", "down", "side", "been",
            "now", "find", "any", "new", "work", "part", "take", "get", "place", "made", "live", "where", "after", "back",
            "little", "only", "round", "man", "year", "came", "show", "every", "good", "me", "give", "our", "under",
            "name", "very", "through", "just", "form", "much", "great", "think", "say", "help", "low", "line", "before",
            "turn", "cause", "same", "mean", "differ", "move", "right", "boy", "old", "too", "does", "tell");

    public static ArrayList<String> parse_doc(Document docHTML, HashMap<String, Boolean> settings) {

        ArrayList<String> tokens = new ArrayList<String>();

        // Get HEAD and BODY text
        tokens.addAll(tokenizeText(docHTML.head().text()));
        tokens.addAll(tokenizeText(docHTML.body().text()));

        // Preprocess the tokens based on settings
        tokens = preprocessTokens(tokens, settings);

        return tokens;

    }

    private static ArrayList<String> tokenizeText(String unprocessedText) {

        // Split the text on space
        String[] splittedText = unprocessedText.split("\\s+");
        return new ArrayList<String>(Arrays.asList(splittedText));

    }

    private static ArrayList<String> preprocessTokens(ArrayList<String> tokens, HashMap<String, Boolean> settings) {

        ArrayList<String> newTokensList = new ArrayList<>();

        // Defines patterns for special chars and digits
        Pattern removeCharsPatrn = Pattern.compile("[`~!›@#$%^&*();»_+[\\\\]\\\\\\\\,/¬{}|:;\"“”…<>?]|^-$|^\\.|\\.+$|.\u200B$|^-");
        Pattern specialCharsEnforce = Pattern.compile("[^\\w+\\-.‑'‘’À-ÿ\\]\\[]+|^'+$|^-+$");
        Pattern findDigitsPatrn = Pattern.compile("\\d+");

        // For each token check settings and preprocess as necessary
        for (String token : tokens) {

            if (settings.get("special_chars")) {
                token = removeCharsPatrn.matcher(token).replaceAll("");
                if (token.trim().equals("") || token.trim().length() == 0 || specialCharsEnforce.matcher(token).find()) {
                    continue;
                }
            }

            if (settings.get("no_numbers")) {
                if (findDigitsPatrn.matcher(token).find()) {
                    continue;
                }
            }

            if (settings.get("case_folding")) {
                token = token.toLowerCase();
            }

            if (settings.get("stop_words_150")) {
                if (STOPWORDSLIST.contains(token)) {
                    continue;
                }
            }

            if (token.trim().equals("")) {
                System.out.println(token);
                continue;
            }

            newTokensList.add(token);

        }

        return newTokensList;

    }
}
