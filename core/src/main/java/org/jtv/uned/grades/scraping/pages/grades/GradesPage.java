package org.jtv.uned.grades.scraping.pages.grades;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jtv.uned.grades.scraping.GradesPageParser;
import org.jtv.uned.grades.scraping.pages.DefaultHeader;
import org.jtv.uned.grades.scraping.pages.gradessearcher.GradesSearcherPage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GradesPage {
    private final String USER_AGENT = "\"Mozilla/5.0 (Windows NT\" +\n" +
            "          \" 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2\"";
    private static final String GRADE_LIST_URL = "https://app.uned.es/gesmatri/Presentacion/modCalificaciones/listadoCalificaciones.aspx";
    private final static Map<String, String> HEADER;
    static {
        HEADER = new HashMap<>(DefaultHeader.getHeader());
        HEADER.put("Accept", "*/*");
        HEADER.put("Accept-Language", "es-ES,es;q=0.9,en;q=0.8,eu;q=0.7");
        HEADER.put("Cache-Control", "no-cache");
        HEADER.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        HEADER.put("Host", "app.uned.es");
        HEADER.put("Referer", "https://app.uned.es/gesmatri/Presentacion/modCalificaciones/listadoCalificaciones.aspx");
        HEADER.put("X-MicrosoftAjax", "Delta=true");
        HEADER.put("X-Requested-With", "XMLHttpRequest");
    }

    private final Map<String, Float> grades;

    public GradesPage(GradesPageParser gradesPageParser, Map<String, String> previousCookies, GradesSearcherPage gradesSearcherPage, int year, int semester) {
        Document response = getParsedResponse(previousCookies, gradesSearcherPage, year, semester);
        this.grades = gradesPageParser.getGrades(response);
    }

    public Map<String, Float> getGrades() {
        return grades;
    }

    private Document getParsedResponse(Map<String, String> previousCookies, GradesSearcherPage gradesSearcherPage, int year, int semester) {
        Document searcherDocument = gradesSearcherPage.getParsedResponse();
        try {
            Connection.Response response = Jsoup.connect(GRADE_LIST_URL)
                    .followRedirects(true)
                    .method(Connection.Method.POST)
                    .userAgent(USER_AGENT)
                    .headers(HEADER)
                    .cookies(previousCookies)
                    .data("__EVENTVALIDATION", searcherDocument.getElementById("__EVENTVALIDATION").val())
                    .data("__VIEWSTATE", searcherDocument.getElementById("__VIEWSTATE").val())
                    .data("__VIEWSTATEGENERATOR", searcherDocument.getElementById("__VIEWSTATEGENERATOR").val())
                    .data("__ASYNCPOST", "true")
                    .data("ctl00_ContentPlaceHolderPrincipal_ScriptManager1_HiddenField", gradesSearcherPage.getHiddenField())
                    .data("ctl00$ContentPlaceHolderPrincipal$btnAceptar", "Aceptar")
                    .data("ctl00$ContentPlaceHolderPrincipal$cmbCriterioEstudios", "01") //Grado
                    .data("ctl00$ContentPlaceHolderPrincipal$cmbCursoAcademico", String.valueOf(year))
                    .data("ctl00$ContentPlaceHolderPrincipal$cpeFiltroCalificaciones_ClientState", "false")
                    .data("ctl00$ContentPlaceHolderPrincipal$cpeInformacion_ClientState", "true")
                    .data("ctl00$ContentPlaceHolderPrincipal$rdbListConvocatoriaEEESyLDI", String.valueOf(semester))
                    .data("ctl00$ContentPlaceHolderPrincipal$ScriptManager1", "ctl00$ContentPlaceHolderPrincipal$updatePanel|ctl00$ContentPlaceHolderPrincipal$btnAceptar")
                    .execute();
            return response.parse();
        } catch (IOException e) {
            throw new GradesPageObtainingException(e);
        }
    }
}
