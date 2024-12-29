package net.mehvahdjukaar.every_compat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTest {

    public static void main(String[] args) {
        final String RES_CHARS = "[a-z,A-Z,\\-,_./]*";
        final Pattern RES_PATTERN = Pattern.compile("\"(" + RES_CHARS + ":" + RES_CHARS + ")\"");

            Matcher matcher = RES_PATTERN.matcher("{\n" +
                    "  \"type\": \"minecraft:block\",\n" +
                    "  \"pools\": [\n" +
                    "    {\n" +
                    "      \"rolls\": 1,\n" +
                    "      \"entries\": [\n" +
                    "        {\n" +
                    "          \"type\": \"minecraft:item\",\n" +
                    "          \"name\": \"supplementaries:awning\"\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"conditions\": [\n" +
                    "        {\n" +
                    "          \"condition\": \"minecraft:survives_explosion\"\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}");
            String news = matcher.replaceAll(m -> {
                var item = m.group(1);
                return item;
               // return item.map(value -> "\"" + Utils.getID(BlockType.changeItemType(value, fromType, toType)).toString() + "\"")
               //         .orElseGet(() -> m.group(0));
            });

            int aa = 1;
    }
}
