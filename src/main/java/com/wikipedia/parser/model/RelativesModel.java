package com.wikipedia.parser.model;

import lombok.Data;

@Data
public class RelativesModel {

    private String url;
    private String name;
    private boolean isReferenced;

    public RelativesModel(String url, String name) {
        this.url = url;
        this.name = name;
        this.isReferenced= !url.equals("");
    }
}
