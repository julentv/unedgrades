package org.jtv.uned.grades.scraping.pages.logging;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jtv.uned.grades.scraping.pages.DefaultHeader;

import java.io.IOException;
import java.util.Map;

public class LoggingPage {
    private static final String USER_AGENT = "\"Mozilla/5.0 (Windows NT\" +\n" +
            "          \" 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2\"";
    private static final String LOGGING_URL = "https://sso.uned.es/sso/index.aspx?URL=https://login.uned.es/ssouned/login.jsp";

    private final String user;
    private final String password;
    private final Map<String, String> cookies;

    public LoggingPage(final String user, final String password) {
        this.user = user;
        this.password = password;
        this.cookies = logIn().cookies();
    }

    public Map<String, String> getLoggedCookies() {
        return cookies;
    }

    private Connection.Response logIn() {
        Connection.Response loggingPage = getLoggingPage();
        try {
            Document loggingPageParsed = loggingPage.parse();
            return Jsoup.connect(LOGGING_URL)
                    .followRedirects(true)
                    .method(Connection.Method.POST)
                    .headers(DefaultHeader.getHeader())
                    .userAgent(USER_AGENT)
                    .cookies(loggingPage.cookies())
                    .data("ctl00$ContentPlaceHolder1$ssousername", user)
                    .data("ctl00$ContentPlaceHolder1$password", password)
                    .data("__EVENTVALIDATION", loggingPageParsed.getElementById("__EVENTVALIDATION").val())
                    .data("__VIEWSTATE", loggingPageParsed.getElementById("__VIEWSTATE").val())
                    .data("__VIEWSTATEGENERATOR", loggingPageParsed.getElementById("__VIEWSTATEGENERATOR").val())
                    .data("ctl00$ContentPlaceHolder1$Button1", "Enviar")
                    .data("ctl00$ContentPlaceHolder1$oraTrace", loggingPageParsed.getElementById("ContentPlaceHolder1_oraTrace").val())
                    .data("ctl00$ContentPlaceHolder1$urlrebote", "https://login.uned.es/ssouned/login.jsp")
                    .execute();
        } catch (IOException e) {
            throw new LoggingPageParsingException(e);
        }
    }

    private Connection.Response getLoggingPage() {
        try {
            return Jsoup.connect(LOGGING_URL).userAgent(USER_AGENT).execute();
        } catch (IOException e) {
            throw new LoggingPageObtainingException(e);
        }
    }
}
