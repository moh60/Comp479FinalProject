package indexer;

import javafx.util.Pair;

import java.io.Serializable;
import java.util.Comparator;

class TreeComparator implements Comparator<Pair<String, Integer>>, Serializable {

    @Override
    public int compare(final Pair<String, Integer> o1, final Pair<String, Integer> o2) {
        return o1.getKey().compareTo(o2.getKey());
    }

}
