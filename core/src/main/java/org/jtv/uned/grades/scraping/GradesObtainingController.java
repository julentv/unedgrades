package org.jtv.uned.grades.scraping;

import org.jtv.uned.grades.scraping.pages.campus.CampusPage;
import org.jtv.uned.grades.scraping.pages.grades.GradesPage;
import org.jtv.uned.grades.scraping.pages.gradessearcher.GradesSearcherPage;
import org.jtv.uned.grades.scraping.pages.logging.LoggingPage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GradesObtainingController {
    private final GradesPageParser gradesPageParser;

    public GradesObtainingController(GradesPageParser gradesPageParser) {
        this.gradesPageParser = gradesPageParser;
    }

    public Map<String, Float> getGrades(String user, String password, int year, int semester) throws IOException {
        Map<String, String> completeCookies = new HashMap<>(new LoggingPage(user, password).getLoggedCookies());
        completeCookies.putAll(new CampusPage(completeCookies).getCookies());

        GradesSearcherPage gradesSearcherPage = new GradesSearcherPage(completeCookies);
        completeCookies.putAll(gradesSearcherPage.getCookies());

        return new GradesPage(gradesPageParser, completeCookies, gradesSearcherPage, year, semester)
                .getGrades();
    }
}
