package org.jtv.uned.grades.scraping.pages.campus;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jtv.uned.grades.scraping.pages.DefaultHeader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CampusPage {
    private static final String USER_AGENT = "\"Mozilla/5.0 (Windows NT\" +\n" +
            "          \" 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2\"";
    private static final String INTERMEDIATE_PAGE_URL = "https://login.uned.es/ssouned/login.jsp";
    private static final String PANNEL_URL = "https://login.uned.es/sso/auth";

    public Map<String, String> getCookies(final Map<String, String> previousCookies) {
        Document intermediatePageParsed = getIntermediatePageParsed(previousCookies);
        Connection.Response campusPageResponse = getCampusPageResponse(previousCookies, intermediatePageParsed);
        return campusPageResponse.cookies();
    }

    private Connection.Response getCampusPageResponse(final Map<String, String> cookies, Document intermediateParse) {
        Map<String, String> headerFinal = new HashMap<>(DefaultHeader.getHeader());
        headerFinal.put("Cache-Control", "max-age=0");
        try {
            return Jsoup.connect(PANNEL_URL)
                    .followRedirects(true)
                    .method(Connection.Method.POST)
                    .userAgent(USER_AGENT)
                    .headers(headerFinal)
                    .cookies(cookies)
                    .data("oratrace", intermediateParse.getElementById("oratrace").val())
                    .data("password", intermediateParse.getElementById("password").val())
                    .data("site2pstoretoken", intermediateParse.getElementsByAttributeValue("NAME", "site2pstoretoken").val())
                    .data("ssocert", intermediateParse.getElementById("ssocert").val())
                    .data("ssousername", intermediateParse.getElementById("ssousername").val())
                    .data("v", intermediateParse.getElementsByAttributeValue("NAME", "v").val())
                    .execute();
        } catch (IOException e) {
            throw new CampusPageObtainingException(e);
        }
    }

    private Document getIntermediatePageParsed(final Map<String, String> cookies) {
        Map<String, String> headerIntermediate = createIntermediatePageHeader();

        try {
            Connection.Response intermediateResponse = Jsoup.connect(INTERMEDIATE_PAGE_URL)
                    .followRedirects(true)
                    .method(Connection.Method.GET)
                    .userAgent(USER_AGENT)
                    .headers(headerIntermediate)
                    .cookies(cookies)
                    .execute();
            return intermediateResponse.parse();
        } catch (IOException e) {
            throw new IntermediatePageObtainigException(e);
        }
    }

    private Map<String, String> createIntermediatePageHeader() {
        Map<String, String> headerIntermediate = new HashMap<>(DefaultHeader.getHeader());
        headerIntermediate.remove("Content-Type");
        return headerIntermediate;
    }
}
