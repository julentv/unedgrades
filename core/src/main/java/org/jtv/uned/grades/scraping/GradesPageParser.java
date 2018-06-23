package org.jtv.uned.grades.scraping;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GradesPageParser {
    private static final Logger LOGGER = Logger.getLogger(GradesPageParser.class);
    private static final String ROW_TAG = "tr";
    private static final String CELL_TAG = "td";

    public Map<String, Float> getGrades(final Document gradesResponseParsed) {
        Element previous = null;
        Element possibleTitle = null;
        Map<String, Float> gradesBySubjectName = new HashMap<>();

        for (final Element row : gradesResponseParsed.getElementsByTag(ROW_TAG)) {
            Elements cells = row.getElementsByTag(CELL_TAG);
            if (cells.size() == 5) {
                if (cells.get(1).text().contains("P (")) {
                    Optional<Float> grade = extractGrade(cells);
                    Optional<String> subjectName = extractSubjectName(possibleTitle);
                    if (grade.isPresent() && subjectName.isPresent()) {
                        gradesBySubjectName.put(subjectName.get(), grade.get());
                    }
                }
            }
            possibleTitle = previous;
            previous = row;
        }

        return gradesBySubjectName;
    }

    private Optional<String> extractSubjectName(final Element possibleTitle) {
        try {
            String text = possibleTitle.getElementsByTag("td").get(1).text();
            int firstSpaceIndex = text.indexOf(" ");

            return Optional.of(text.substring(firstSpaceIndex).trim());
        } catch (Exception e) {
            LOGGER.error("Error extracting subject name", e);
        }
        return Optional.empty();
    }

    private Optional<Float> extractGrade(final Elements cells) {
        try {
            String text = cells.get(2).text();
            String[] split = text.split(": ");
            if (split.length > 1) {
                return Optional.of(Float.valueOf(split[1].trim().replace(",", ".")));
            }
        } catch (Exception e) {
            LOGGER.error("Error extracting grade", e);
        }
        return Optional.empty();
    }
}
