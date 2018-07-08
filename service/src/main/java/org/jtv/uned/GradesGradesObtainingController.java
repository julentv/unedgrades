package org.jtv.uned;

import org.apache.log4j.Logger;
import org.jtv.uned.grades.Grades;
import org.jtv.uned.grades.GradesObtainingService;
import org.jtv.uned.grades.scraping.GradesObtainingController;
import org.jtv.uned.grades.scraping.GradesPageParser;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;

@RestController
public class GradesGradesObtainingController {
    private static final Logger LOGGER = Logger.getLogger(GradesObtainingService.class);

    @RequestMapping("/")
    public String index() {
        return "pong";
    }

    @ExceptionHandler({GradesObtainingException.class})
    @RequestMapping("/grades")
    public Grades getGrades(@RequestParam(value = "name") String name, @RequestParam(value = "password") String password, @RequestParam(value = "year", required = false) Integer year, @RequestParam(value = "semester", required = false) Integer semester) throws GradesObtainingException {
        try {
            LOGGER.info("getting grades");
            GradesObtainingService gradesObtainingService = new GradesObtainingService(name, password, new GradesObtainingController(new GradesPageParser()));
            return gradesObtainingService.obtainGrades(getYear(year), getSemester(semester));
        } catch (RuntimeException e) {
            throw new GradesObtainingException(e);
        }
    }

    private int getYear(Integer year) {
        if (year != null) {
            return year;
        }
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    private int getSemester(Integer semester) {
        if (semester != null) {
            return semester;
        }
        if (Calendar.getInstance().get(Calendar.MONTH) < 5) {
            return 1;
        }
        return 2;
    }
}
