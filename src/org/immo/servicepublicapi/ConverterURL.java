package org.immo.servicepublicapi;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ConverterURL {
    String query;
    String encodedQuery;

    public ConverterURL (String query) {
        this.query=query;
        String refinedQuery = removeWhiteSpace(query);
        this.encodedQuery = URLEncoder.encode(refinedQuery, StandardCharsets.UTF_8);
    }

    private String removeWhiteSpace(String query) {
        return query.replaceAll("\\s","" );
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getEncodedQuery() {
        return encodedQuery;
    }

    public void setEncodedQuery(String encodedQuery) {
        this.encodedQuery = encodedQuery;
    }
}
