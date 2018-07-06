package org.jtv.uned.grades.scraping;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Map;

@Ignore
public class LocalOnlyTest {
    @Test
    public void getGrades() throws Exception {
        GradesScraper gradesScraper = new GradesScraper(new GradesPageParser());
        Map<String, Float> grades = gradesScraper.getGrades("jtelleria6", "julen300591", 2018, 1);

        Assert.assertEquals(6.1f, grades.get("Psicofarmacología"), 0);
        Assert.assertEquals(6f, grades.get("Psicología del Desarrollo II"), 0);
        Assert.assertEquals(6.6f, grades.get("Psicología de los Grupos"), 0);
        Assert.assertEquals(6.7f, grades.get("Psicología del Lenguaje"), 0);
        Assert.assertEquals(3.5f, grades.get("Evaluación Psicológica"), 0);

        System.out.println("Grades correctly obtained");
    }

}