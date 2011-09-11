package com.example.levellist;

public final class LevelListIconMaker {

    private LevelListIconMaker() {
    }

    public static void main(String[] args) {

        System.out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        System.out.println("<level-list xmlns:android=\"http://schemas.android.com/apk/res/android\">");

        for (int i = 0; i < 100; ++i) {

            String in = String.format("  <item android:maxLevel=\"%d\">", i);
            String lv = String.format(layer, i / 10, i % 10);
            String out = "  </item>";

            System.out.println(in);
            System.out.println(lv);
            System.out.println(out);

        }

        System.out.println("</level-list>");
    }

    static String layer = 
    "    <layer-list>\n"
            +"      <item>\n"
            +"        <bitmap android:src=\"@drawable/b\" android:gravity=\"center\" />\n"
            +"      </item>\n"
            +"      <item>\n"
            +"        <bitmap android:src=\"@drawable/n%d\" android:gravity=\"left\" />\n"
            +"      </item>\n"
            +"      <item>\n"
            +"        <bitmap android:src=\"@drawable/n%d\" android:gravity=\"right\" />\n"
            +"      </item>\n"
            +"    </layer-list>";
}
