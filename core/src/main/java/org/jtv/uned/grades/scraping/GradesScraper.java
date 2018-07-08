package org.jtv.uned.grades.scraping;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jtv.uned.grades.scraping.pages.DefaultHeader;
import org.jtv.uned.grades.scraping.pages.campus.CampusPage;
import org.jtv.uned.grades.scraping.pages.gradessearcher.GradesSearcherPage;
import org.jtv.uned.grades.scraping.pages.logging.LoggingPage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GradesScraper {
    private final String USER_AGENT = "\"Mozilla/5.0 (Windows NT\" +\n" +
            "          \" 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2\"";
    private static final String PANNEL = "https://login.uned.es/sso/auth";

    private final HashMap<String, String> defaultHeader;
    private final GradesPageParser gradesPageParser;

    public GradesScraper(GradesPageParser gradesPageParser) {
        this.gradesPageParser = gradesPageParser;
        this.defaultHeader = DefaultHeader.getHeader();
    }

    public Map<String, Float> getGrades(String user, String password, int year, int semester) throws IOException {
        Map<String, String> completeCookies = new HashMap<>(new LoggingPage(user, password).getLoggedCookies());
        completeCookies.putAll(new CampusPage().getCookies(completeCookies));

        GradesSearcherPage gradesSearcherPage = new GradesSearcherPage(completeCookies);

        Document searcherDocument = gradesSearcherPage.getParsedResponse();
        completeCookies.putAll(gradesSearcherPage.getCookies());

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
                .data("ctl00_ContentPlaceHolderPrincipal_ScriptManager1_HiddenField", gradesSearcherPage.getHiddenField())
                .data("ctl00$ContentPlaceHolderPrincipal$btnAceptar", "Aceptar")
                .data("ctl00$ContentPlaceHolderPrincipal$cmbCriterioEstudios", "01") //Grado
                .data("ctl00$ContentPlaceHolderPrincipal$cmbCursoAcademico", String.valueOf(year))
                .data("ctl00$ContentPlaceHolderPrincipal$cpeFiltroCalificaciones_ClientState", "false")
                .data("ctl00$ContentPlaceHolderPrincipal$cpeInformacion_ClientState", "true")
                .data("ctl00$ContentPlaceHolderPrincipal$rdbListConvocatoriaEEESyLDI", String.valueOf(semester)) //Junio
                .data("ctl00$ContentPlaceHolderPrincipal$ScriptManager1", "ctl00$ContentPlaceHolderPrincipal$updatePanel|ctl00$ContentPlaceHolderPrincipal$btnAceptar")
                .execute();

        Document gradesResponseParsed = getGradesResponse.parse();

        return gradesPageParser.getGrades(gradesResponseParsed);
    }
}
