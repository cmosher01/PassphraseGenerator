package nu.mine.mosher.security.password;

import java.io.*;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.join;

public class PassPhraseGenerator {
    public static void main(final String... args) throws IOException {
        final int COUNT_WORDS_GENERATE = 6;

        final int countWordsDictionary = countWordsInDictionary();

        final List<Integer> chosenIndexes = chooseIndexes(COUNT_WORDS_GENERATE, countWordsDictionary);

        final List<String> generated = getWords(chosenIndexes);

        System.out.println(join(" ", generated));
        System.out.flush();
    }

    private static List<Integer> chooseIndexes(final int countWordsGenerate, final int countWordsDictionary) {
        if (countWordsDictionary == 0) {
            throw new IllegalArgumentException("Empty dictionary.");
        }
        return new SecureRandom()
            .ints(countWordsGenerate, 0, countWordsDictionary)
            .boxed()
            .collect(Collectors.toList());
    }

    private static List<String> getWords(final List<Integer> chosen) throws IOException {
        final String[] w = new String[chosen.size()];
        try (final LineNumberReader dict = new LineNumberReader(dictionary())) {
            filterWords(dict, chosen, w);
        }
        return List.of(w);
    }

    private static void filterWords(final LineNumberReader in, final List<Integer> chosen, final String[] w) throws IOException {
        for (String lin = in.readLine(); lin != null; lin = in.readLine()) {
            final int n = in.getLineNumber()-1;
            for (int at = chosen.indexOf(n); 0 <= at; at = chosen.indexOf(n)) {
                w[at] = extractWord(lin);
                chosen.set(at, null);
            };
        }
    }

    private static final String REGEX_DICEWARE_FORMAT = "^\\d+\\s+(.+)$";

    private static String extractWord(final String lin) {
        return lin.replaceFirst(REGEX_DICEWARE_FORMAT, "$1");
    }

    private static int countWordsInDictionary() throws IOException {
        try (final BufferedReader dict = new BufferedReader(dictionary())) {
            return (int)dict.lines().count();
        }
    }

    private static InputStreamReader dictionary() {
        return new InputStreamReader(PassPhraseGenerator.class.getResourceAsStream("/dictionary.txt"));
    }
}
