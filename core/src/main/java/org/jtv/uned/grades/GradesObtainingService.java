package org.jtv.uned.grades;

import org.apache.log4j.Logger;
import org.jtv.uned.grades.scraping.GradesScraper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GradesObtainingService {
    private static final Logger LOGGER = Logger.getLogger(GradesObtainingService.class);

    private final String user;
    private final String password;
    private final GradesScraper gradesScraper;

    public GradesObtainingService(String user, String password, GradesScraper gradesScraper) {
        this.user = user;
        this.password = password;
        this.gradesScraper = gradesScraper;
    }

    public Grades obtainGrades(int year, int semester) {
        Map<String, Float> grades = new HashMap<>();
        try {
            grades.putAll(gradesScraper.getGrades(user, password));
        } catch (IOException e) {
            LOGGER.error(e);
        }
        return new Grades(user, year, semester, grades);
    }

}