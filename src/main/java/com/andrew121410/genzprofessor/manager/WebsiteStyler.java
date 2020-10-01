package com.andrew121410.genzprofessor.manager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class WebsiteStyler {

    private static final String NEW_LINE = "\r\n";

    public WebsiteStyler() {

    }

    public Document format(Document document) {
        StringBuilder stringBuilder = new StringBuilder("<!doctype html>");
        stringBuilder.append(NEW_LINE);
        stringBuilder.append("<html lang=\"en\">");
        stringBuilder.append("<body>");

        stringBuilder.append("<style>" + NEW_LINE +
                ".accordion {\n" +
                "background-color: #fc2403;\n" +
                "color: #00000;\n" +
                "cursor: pointer;\n" +
                "padding: 15px;\n" +
                "width: 100%;\n" +
                "border: solid black 1px;\n" +
                "text-align: left;\n" +
                "font-size: 15px;\n" +
                "height:auto;\n" +
                "overflow:hidden;\n" +
                "filter: brightness(100%);\n" +
                "transition:filter 0.15s;\n" +
                "}" + NEW_LINE +
                ".accordion:hover {\n" +
                "filter: brightness(125%);\n" +
                "border: solid black;\n" +
                "}" + NEW_LINE +
                "body {\n" +
                "background-color: rgb(0, 180, 186);\n" +
                "}" + NEW_LINE +
                "</style>");

        //Question button
        stringBuilder.append("<button class=\"accordion active\"> <b>Question:</b> </button>");
        stringBuilder.append("<div class=\"imautistic\"> </div>");
        //Answer button
        stringBuilder.append("<button class=\"accordion active\"> <b>Answer:</b> </button>");
        stringBuilder.append("<div class=\"iamreallythough\"> </div>");
        stringBuilder.append("</body>");
        stringBuilder.append("</html>");

        Document ourHtmlDoc = Jsoup.parse(stringBuilder.toString());
        Elements elements = ourHtmlDoc.getElementsByClass("imautistic");
        elements.append(document.getElementsByClass("ugc-base question-body-text").toString());
        Elements elements1 = ourHtmlDoc.getElementsByClass("iamreallythough");
        elements1.append(document.getElementsByClass("answer-given-body ugc-base").toString());
        return ourHtmlDoc;
    }
}
