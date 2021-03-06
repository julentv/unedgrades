package org.jtv.uned.grades;

import org.jtv.uned.grades.scraping.GradesObtainingController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.HashMap;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class GradesObtainingServiceTest {

    private static final String USER = "user";
    private static final String PASSWORD = "pass";
    @Mock
    private GradesObtainingController gradesObtainingController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void whenObtainingGrades_basicDataIsObtained() throws IOException {
        HashMap<String, Float> gradesBySubjectName = new HashMap<>();
        gradesBySubjectName.put("subject1", 6f);
        when(gradesObtainingController.getGrades(eq(USER), eq(PASSWORD), eq(2018), eq(1))).thenReturn(gradesBySubjectName);
        GradesObtainingService gradesObtainingService = new GradesObtainingService(USER, PASSWORD, gradesObtainingController);
        Grades grades = gradesObtainingService.obtainGrades(2018, 1);
        Assert.assertEquals(USER, grades.getUser());
        Assert.assertEquals(2018, grades.getYear());
        Assert.assertEquals(1, grades.getSemester());
        Assert.assertEquals(6f, grades.getGrades().get("subject1"), 0);
    }
}
