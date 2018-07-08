package org.jtv.uned.grades;

import org.apache.log4j.Logger;
import org.jtv.uned.grades.scraping.GradesObtainingController;

import java.util.HashMap;
import java.util.Map;

public class GradesObtainingService {
    private static final Logger LOGGER = Logger.getLogger(GradesObtainingService.class);

    private final String user;
    private final String password;
    private final GradesObtainingController gradesObtainingController;

    public GradesObtainingService(String user, String password, GradesObtainingController gradesObtainingController) {
        this.user = user;
        this.password = password;
        this.gradesObtainingController = gradesObtainingController;
    }

    public Grades obtainGrades(int year, int semester) {
        Map<String, Float> grades = new HashMap<>();
        try {
            grades.putAll(gradesObtainingController.getGrades(user, password, year, semester));
        } catch (Exception e) {
            LOGGER.error("Error getting grades", e);
        }
        return new Grades(user, year, semester, grades);
    }

}
