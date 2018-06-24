package org.jtv.uned;

import org.apache.log4j.Logger;
import org.jtv.uned.grades.Grades;
import org.jtv.uned.grades.GradesObtainingService;
import org.jtv.uned.grades.scraping.GradesPageParser;
import org.jtv.uned.grades.scraping.GradesScraper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class GradesGradesObtainingController {
    private static final Logger LOGGER = Logger.getLogger(GradesObtainingService.class);

    @RequestMapping("/grades")
    public Grades greeting(@RequestParam(value = "name") String name, @RequestParam(value = "password") String password) {
        LOGGER.info("getting grades");
        GradesObtainingService gradesObtainingService = new GradesObtainingService(name, password, new GradesScraper(new GradesPageParser()));
        return gradesObtainingService.obtainGrades(2018, 1);
    }
}
