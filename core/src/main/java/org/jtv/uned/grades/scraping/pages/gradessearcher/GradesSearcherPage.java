package org.jtv.uned.grades.scraping.pages.gradessearcher;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jtv.uned.grades.scraping.pages.DefaultHeader;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

public class GradesSearcherPage {
    private static final String GRADES_SEARCHER_URL = "https://app.uned.es/gesmatri/Presentacion/modCalificaciones/listadoCalificaciones.aspx";
    private static final String USER_AGENT = "\"Mozilla/5.0 (Windows NT\" +\n" +
            "          \" 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2\"";
    private static final Map<String, String> HEADERS;

    private final Map<String, String> cookies;
    private final Document parsedReponse;

    static {
        HEADERS = new HashMap<>(DefaultHeader.getHeader());
        HEADERS.put("Cache-Control", "max-age=0");
        HEADERS.put("Host", "app.uned.es");
        HEADERS.put("Referer", "http://portal.uned.es/portal/page?_pageid=93,153054&_dad=portal&_schema=PORTAL");
    }

    public GradesSearcherPage(final Map<String, String> previousCookies) {
        try {
            Connection.Response response = Jsoup.connect(GRADES_SEARCHER_URL)
                    .followRedirects(true)
                    .method(Connection.Method.GET)
                    .userAgent(USER_AGENT)
                    .headers(HEADERS)
                    .cookies(previousCookies)
                    .execute();
            this.parsedReponse = response.parse();
            this.cookies = response.cookies();
        } catch (IOException e) {
            throw new GradesSearcherObtainingException(e);
        }
    }

    public Document getParsedResponse() {
        return this.parsedReponse;
    }

    public String getHiddenField() {

        try {

            ListIterator<Element> scriptIterator = this.parsedReponse.getElementsByTag("script").listIterator();
            String hiddenFieldValue = "";
            while (scriptIterator.hasNext()) {
                Element element = scriptIterator.next();
                if (element.attr("src").contains("ctl00_ContentPlaceHolderPrincipal_ScriptManager1_HiddenField")) {
                    String src = element.attr("src");
                    int start = src.indexOf("_TSM_CombinedScripts_=");
                    hiddenFieldValue = URLDecoder.decode(src.substring(start + "_TSM_CombinedScripts_=".length()), "UTF-8");
                }
            }
            return hiddenFieldValue;
        } catch (IOException e) {
            throw new GradesSearcherParsingException(e);
        }
    }

    public Map<String, String> getCookies() {
        return this.cookies;
    }
}
