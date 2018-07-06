package org.jtv.uned.grades.scraping.pages;

import java.util.HashMap;

public class DefaultHeader {

    private static final HashMap<String, String> HEADER_VALUES;

    static {
        HEADER_VALUES = new HashMap<>();
        HEADER_VALUES.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        HEADER_VALUES.put("Accept-Encoding", "gzip, deflate, br");
        HEADER_VALUES.put("Accept-Language", "es-MX,es;q=0.8,en-US;q=0.5,en;q=0.3");
        HEADER_VALUES.put("Connection", "keep-alive");
        HEADER_VALUES.put("Content-Type", "application/x-www-form-urlencoded");
        HEADER_VALUES.put("Host", "sso.uned.es");
        HEADER_VALUES.put("Referer", "https://sso.uned.es/sso/index.aspx?URL=https://login.uned.es/ssouned/login.jsp");
        HEADER_VALUES.put("Upgrade-Insecure-Requests", "1");
    }

    public static HashMap<String, String> getHeader() {
        return new HashMap<>(HEADER_VALUES);
    }
}
