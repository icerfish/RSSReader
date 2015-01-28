package com.icerfish.rssreader.data;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dylanturney on 26/01/15.
 */
public class ArticleXMLParser {

    public static String TAG = ArticleXMLParser.class.getSimpleName();

    private static final String ns = null;

    public List parse(String xmlString) throws XmlPullParserException, IOException {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new StringReader(xmlString));
            parser.nextTag();
            return readFeed(parser);
    }

    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List items = new ArrayList();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("channel")) {
                items.addAll(readChannel(parser));
            } else {
                skip(parser);
            }
        }

        return items;
    }


    private List<Article> readChannel(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        List<Article> items = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, null, "channel");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("item")) {
                items.add(readItem(parser));
            } else {
                skip(parser);
            }
        }
        return items;
    }


    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    private Article readItem(XmlPullParser parser) throws XmlPullParserException, IOException {

        parser.require(XmlPullParser.START_TAG, ns, "item");
        String title = null;
        String description = null;
        String link = null;
        String pubDate = null;
        String thumbnailUrlSmall = null;
        String thumbnailUrlLarge = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            switch (name) {
                case "title":
                    title = readTag(parser, "title");
                    break;
                case "description":
                    description = readTag(parser, "description");
                    break;
                case "link":
                    link = readTag(parser, "link");
                    break;
                case "pubDate":
                    pubDate = readTag(parser, "pubDate");
                    break;
                case "media:thumbnail":
                    if (thumbnailUrlSmall == null)
                        thumbnailUrlSmall = readThumbnail(parser);
                    else
                        thumbnailUrlLarge = readThumbnail(parser);
                default:
                    skip(parser);
                    break;

            }
        }

        return new Article(title, description, link, pubDate, thumbnailUrlSmall, thumbnailUrlLarge);
    }

    // Processes title tags in the feed.
    private String readTag(XmlPullParser parser, String tagName) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tagName);
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tagName);
        return title;
    }

    private String readThumbnail(XmlPullParser parser) throws IOException, XmlPullParserException {
        String thumbnailUrl = "";
        parser.require(XmlPullParser.START_TAG, ns, "media:thumbnail");
        thumbnailUrl = parser.getAttributeValue(null, "url");
//        parser.require(XmlPullParser.END_TAG, ns, "media:thumbnail");
        return thumbnailUrl;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
