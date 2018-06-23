package org.jtv.uned.grades.scraping;

import org.apache.commons.io.IOUtils;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class GradesPageParserTest {

    @Test
    public void getGrades() throws Exception {
        String xml = IOUtils.toString(this.getClass().getResourceAsStream("gradesTable.html"), "UTF-8");
        Document parse = Parser.parse(xml, "test.com");

        GradesPageParser gradesPageParser = new GradesPageParser();
        Map<String, Float> grades = gradesPageParser.getGrades(parse);

        Assert.assertEquals(6.23f, grades.get("Psicofarmacología"), 0);
        Assert.assertEquals(6f, grades.get("Psicología del Desarrollo II"), 0);
        Assert.assertEquals(6.6f, grades.get("Psicología de los Grupos"), 0);
        Assert.assertEquals(6.7f, grades.get("Psicología del Lenguaje"), 0);
        Assert.assertEquals(3.5f, grades.get("Evaluación Psicológica"), 0);
    }

}