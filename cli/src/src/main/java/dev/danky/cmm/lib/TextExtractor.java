package dev.danky.cmm.lib;

// had an idea with this but never used it but might do it later

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextExtractor {
    public String[] extractText(String str) {
        String[] occurrences = {};

        Pattern p = Pattern.compile("\"([\"'])(?:(?=(\\\\\\\\?))\\\\2.)*?\\\\1\"");
        Matcher m = p.matcher(str);

        if (m.find()) {
            System.out.println(m.groupCount());
        } else {
            System.out.println("nothing");
        }

        return occurrences;
    }
}
