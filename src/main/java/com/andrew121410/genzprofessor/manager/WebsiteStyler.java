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
        //Question button
        stringBuilder.append("<button class=\"accordion active\"> <b>Question:</b> </button>");
        stringBuilder.append("<div class=\"imautistic\"> </div>");
        //Answer button
        stringBuilder.append("<button class=\"accordion active\"> <b>Answer:</b> </button>");
        stringBuilder.append("<div class=\"iamreallythough\"> </div>");

        stringBuilder.append("\n" +
                "\n" +
                "<script>\n" +
                "    var acc = document.getElementsByClassName(\"accordion\");\n" +
                "    var i;\n" +
                "    for (i = 0; i < acc.length; i++) {\n" +
                "        acc[i].addEventListener(\"click\", function () {\n" +
                "            //panel.style.height = \"0\";\n" +
                "            //this.nextElementSibling.innerHTML =JSON.stringify(this.nextElementSibling.className);\n" +
                "            this.nextElementSibling.classList.toggle('collapse')\n" +
                "            this.nextElementSibling.classList.toggle('expand')\n" +
                "            //alert(this.nextElementSibling)\n" +
                "\n" +
                "        });\n" +
                "    }\n" +
                "</script>");

        stringBuilder.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"><style>\n" +
                "\n" +
                "    .accordion {\n" +
                "        background-color: #ED8C00;\n" +
                "        color: #00000;\n" +
                "        cursor: pointer;\n" +
                "        padding: 15px;\n" +
                "        width: 100%;\n" +
                "        border: solid black 1px;\n" +
                "        text-align: left;\n" +
                "        font-size: 15px;\n" +
                "        height:auto;\n" +
                "        overflow:hidden;\n" +
                "        filter: brightness(100%);\n" +
                "        transition:filter 0.15s;\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    .accordion:hover {\n" +
                "        filter: brightness(125%);\n" +
                "        border: solid black;\n" +
                "    }\n" +
                "\n" +
                "    .panel {\n" +
                "        background-color: white;\n" +
                "        height:auto;\n" +
                "        opacity: 1;\n" +
                "        padding: 0 18px;\n" +
                "        max-height:500em;\n" +
                "        border-style: groove;\n" +
                "        transition: max-height 0.5s ease;\n" +
                "        overflow:hidden;\n" +
                "    }\n" +
                "\n" +
                "    .panel.colapse {\n" +
                "        max-height:0em;\n" +
                "        border-style: none;\n" +
                "    }\n" +
                "\n" +
                "    .panel.expand{\n" +
                "        max-height:500em;\n" +
                "        border-style: groove;\n" +
                "    }\n" +
                "\n" +
                "    .answer {\n" +
                "        height:auto;\n" +
                "        max-height:500em;\n" +
                "        transition: max-height 0.5s ease;\n" +
                "    }\n" +
                "\n" +
                "    .answer.colapse {\n" +
                "        max-height:0em;\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    .answer.expand{\n" +
                "        max-height:500em;\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "    .question {\n" +
                "        padding: 0 18px;\n" +
                "        border: groove;\n" +
                "        overflow:hidden;\n" +
                "    }\n" +
                "    .main {\n" +
                "        background-color: white;\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "</style>");

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
