package org.jtv.uned.grades.scraping;

import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

public class GradesScraper {
    private static final Logger LOGGER = Logger.getLogger(GradesScraper.class);

    final String USER_AGENT = "\"Mozilla/5.0 (Windows NT\" +\n" +
            "          \" 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2\"";
    private static final String LOGGING_URL = "https://sso.uned.es/sso/index.aspx?URL=https://login.uned.es/ssouned/login.jsp";
    private static final String PANNEL = "https://login.uned.es/sso/auth";

    private final HashMap<String, String> defaultHeader;
    private final GradesPageParser gradesPageParser;

    public GradesScraper(GradesPageParser gradesPageParser) {
        this.gradesPageParser = gradesPageParser;

        defaultHeader = new HashMap<>();
        defaultHeader.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        defaultHeader.put("Accept-Encoding", "gzip, deflate, br");
        defaultHeader.put("Accept-Language", "es-MX,es;q=0.8,en-US;q=0.5,en;q=0.3");
        defaultHeader.put("Connection", "keep-alive");
        defaultHeader.put("Content-Type", "application/x-www-form-urlencoded");
        defaultHeader.put("Host", "sso.uned.es");
        defaultHeader.put("Referer", "https://sso.uned.es/sso/index.aspx?URL=https://login.uned.es/ssouned/login.jsp");
        defaultHeader.put("Upgrade-Insecure-Requests", "1");
    }

    public Map<String, Float> getGrades(String user, String password) throws IOException {
        Connection.Response loggingResponse = insertLoggingData(user, password);
        Map<String, String> completeCookies = new HashMap<>(loggingResponse.cookies());
        Connection.Response campusResponse = accessToCampus(loggingResponse);
        completeCookies.putAll(campusResponse.cookies());

        Map<String, String> gradeSearchHeaders = new HashMap<>(defaultHeader);
        gradeSearchHeaders.put("Cache-Control", "max-age=0");
        gradeSearchHeaders.put("Host", "app.uned.es");
        gradeSearchHeaders.put("Referer", "http://portal.uned.es/portal/page?_pageid=93,153054&_dad=portal&_schema=PORTAL");
        Connection.Response gradeSearcherResponse = Jsoup.connect("https://app.uned.es/gesmatri/Presentacion/modCalificaciones/listadoCalificaciones.aspx")
                .followRedirects(true)
                .method(Connection.Method.GET)
                .userAgent(USER_AGENT)
                .headers(gradeSearchHeaders)
                .cookies(completeCookies)
                .execute();

        Document searcherDocument = gradeSearcherResponse.parse();

        String hiddenFieldValue = getHiddenField(searcherDocument);

        completeCookies.putAll(gradeSearcherResponse.cookies());


        Map<String, String> getGradesRHeaders = new HashMap<>(defaultHeader);
        getGradesRHeaders.put("Accept", "*/*");
        getGradesRHeaders.put("Accept-Language", "es-ES,es;q=0.9,en;q=0.8,eu;q=0.7");
        getGradesRHeaders.put("Cache-Control", "no-cache");
        getGradesRHeaders.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        getGradesRHeaders.put("Host", "app.uned.es");
        getGradesRHeaders.put("Referer", "https://app.uned.es/gesmatri/Presentacion/modCalificaciones/listadoCalificaciones.aspx");
        getGradesRHeaders.put("X-MicrosoftAjax", "Delta=true");
        getGradesRHeaders.put("X-Requested-With", "XMLHttpRequest");

        Connection.Response getGradesResponse = Jsoup.connect("https://app.uned.es/gesmatri/Presentacion/modCalificaciones/listadoCalificaciones.aspx")
                .followRedirects(true)
                .method(Connection.Method.POST)
                .userAgent(USER_AGENT)
                .headers(getGradesRHeaders)
                .cookies(completeCookies)
                .data("__EVENTVALIDATION", searcherDocument.getElementById("__EVENTVALIDATION").val())
                .data("__VIEWSTATE", searcherDocument.getElementById("__VIEWSTATE").val())
                .data("__VIEWSTATEGENERATOR", searcherDocument.getElementById("__VIEWSTATEGENERATOR").val())
                .data("__ASYNCPOST", "true")
                .data("ctl00_ContentPlaceHolderPrincipal_ScriptManager1_HiddenField", hiddenFieldValue)
                .data("ctl00$ContentPlaceHolderPrincipal$btnAceptar", "Aceptar")
                .data("ctl00$ContentPlaceHolderPrincipal$cmbCriterioEstudios", "01") //Grado
                .data("ctl00$ContentPlaceHolderPrincipal$cmbCursoAcademico", "2018")
                .data("ctl00$ContentPlaceHolderPrincipal$cpeFiltroCalificaciones_ClientState", "false")
                .data("ctl00$ContentPlaceHolderPrincipal$cpeInformacion_ClientState", "true")
                .data("ctl00$ContentPlaceHolderPrincipal$rdbListConvocatoriaEEESyLDI", "1") //Junio
                .data("ctl00$ContentPlaceHolderPrincipal$ScriptManager1", "ctl00$ContentPlaceHolderPrincipal$updatePanel|ctl00$ContentPlaceHolderPrincipal$btnAceptar")
                .execute();

        Document gradesResponseParsed = getGradesResponse.parse();
//        LOGGER.info(gradesResponseParsed.outerHtml());

        return gradesPageParser.getGrades(gradesResponseParsed);
    }

    private String getHiddenField(final Document searcherDocument) throws UnsupportedEncodingException {
        ListIterator<Element> scriptIterator = searcherDocument.getElementsByTag("script").listIterator();
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
    }

    private Connection.Response insertLoggingData(String user, String password) throws IOException {
        Connection.Response init = Jsoup.connect(LOGGING_URL).userAgent(USER_AGENT).execute();

        Document initParsed = init.parse();

        return Jsoup.connect(LOGGING_URL)
                .followRedirects(true)
                .method(Connection.Method.POST)
                .headers(defaultHeader)
                .userAgent(USER_AGENT)
                .cookies(init.cookies())
                .data("ctl00$ContentPlaceHolder1$ssousername", user)
                .data("ctl00$ContentPlaceHolder1$password", password)
                .data("__EVENTVALIDATION", initParsed.getElementById("__EVENTVALIDATION").val())
                .data("__VIEWSTATE", initParsed.getElementById("__VIEWSTATE").val())
                .data("__VIEWSTATEGENERATOR", initParsed.getElementById("__VIEWSTATEGENERATOR").val())
                .data("ctl00$ContentPlaceHolder1$Button1", "Enviar")
                .data("ctl00$ContentPlaceHolder1$oraTrace", initParsed.getElementById("ContentPlaceHolder1_oraTrace").val())
                .data("ctl00$ContentPlaceHolder1$urlrebote", "https://login.uned.es/ssouned/login.jsp")
                .execute();
    }

    private Connection.Response accessToCampus(final Connection.Response response) throws IOException {

        Map<String, String> headerIntermediate = new HashMap<>(defaultHeader);
        headerIntermediate.remove("Content-Type");

        Connection.Response intermediateResponse = Jsoup.connect("https://login.uned.es/ssouned/login.jsp")
                .followRedirects(true)
                .method(Connection.Method.GET)
                .userAgent(USER_AGENT)
                .headers(headerIntermediate)
                .cookies(response.cookies())
                .execute();
        Document intermediateParse = intermediateResponse.parse();

        Map<String, String> headerFinal = new HashMap<>(defaultHeader);
        headerFinal.put("Cache-Control", "max-age=0");
        Connection.Response finalResponse = Jsoup.connect(PANNEL)
                .followRedirects(true)
                .method(Connection.Method.POST)
                .userAgent(USER_AGENT)
                .headers(headerFinal)
                .cookies(response.cookies())
                .data("oratrace", intermediateParse.getElementById("oratrace").val())
                .data("password", intermediateParse.getElementById("password").val())
                .data("site2pstoretoken", intermediateParse.getElementsByAttributeValue("NAME", "site2pstoretoken").val())
                .data("ssocert", intermediateParse.getElementById("ssocert").val())
                .data("ssousername", intermediateParse.getElementById("ssousername").val())
                .data("v", intermediateParse.getElementsByAttributeValue("NAME", "v").val())
                .execute();
        return finalResponse;
    }
}
