package com.example.studreminder;

import android.content.Context;
import android.net.Uri;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class WordImporter {

    /**
     * Extracts plain text from a .docx file.
     * .docx files are essentially ZIP files containing XML documents.
     */
    public static String extractTextFromDocx(Context context, Uri uri) {
        StringBuilder text = new StringBuilder();
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            if (is == null) return "";

            ZipInputStream zis = new ZipInputStream(is);
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                // The main content of a Word document is in word/document.xml
                if (entry.getName().equals("word/document.xml")) {
                    text.append(parseWordXml(zis));
                    break;
                }
            }
            zis.close();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error extracting text: " + e.getMessage();
        }
        return text.toString().trim();
    }

    private static String parseWordXml(InputStream is) throws Exception {
        StringBuilder sb = new StringBuilder();
        XmlPullParser parser = Xml.newPullParser();
        // Word XML uses namespaces, but we can process it simply by ignoring them
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(is, null);

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                String name = parser.getName();
                if (name != null) {
                    // Check for tags both with and without namespace prefix
                    if (name.endsWith("t") && name.length() <= 3) { // Matches 't' or 'w:t'
                        sb.append(parser.nextText());
                    } else if (name.endsWith("p") && name.length() <= 3) { // Matches 'p' or 'w:p'
                        sb.append("\n");
                    }
                }
            }
            eventType = parser.next();
        }
        return sb.toString();
    }
}
