package org.jtv.uned.grades;

import java.util.Map;

public class Grades {
    private final String user;
    private final int year;
    private final int semester;
    private final Map<String, Float> grades;

    public Grades(final String user, final int year, final int semester, final Map<String, Float> grades) {
        this.user = user;
        this.year = year;
        this.semester = semester;
        this.grades = grades;
    }

    public String getUser() {
        return user;
    }

    public int getYear() {
        return year;
    }

    public int getSemester() {
        return semester;
    }

    public Map<String, Float> getGrades() {
        return grades;
    }
}
