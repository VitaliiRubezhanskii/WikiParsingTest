package com.wikipedia.parser.service;

import com.wikipedia.parser.model.RelativesModel;
import java.util.List;
import java.util.Map;

public interface ParsingService {
    /**
     *  parseSite method is aimed to walk through Wiki page and find any info in Personal Details block
     *  relevant to relatives Spouse(s), Children, Parents
     **/
    Map<String,List<RelativesModel>> parseSite(String siteUrl);
}
