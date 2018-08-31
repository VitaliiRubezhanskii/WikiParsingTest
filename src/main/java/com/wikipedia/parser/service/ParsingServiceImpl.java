package com.wikipedia.parser.service;

import com.wikipedia.parser.exceptions.HtmlParsingException;
import com.wikipedia.parser.model.RelativesModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wikipedia.parser.model.HtmlTags.*;

@Service
public class ParsingServiceImpl implements ParsingService {

    @Value("${spring.relatives.data}")
    private String personalDetails;
    @Value("${spring.relatives.group.parents}")
    private String parents;
    @Value("${spring.relatives.group.children}")
    private String children;
    @Value("${spring.relatives.group.spouses}")
    private String spouses;
    @Value("${spring.wikipedia.url}")
    private String wikipediaPrefixUrl;

    @Override
    public Map<String, List<RelativesModel>> parseSite(String siteUrl) {
        Map<String, List<RelativesModel>> relativeModelsMap = new HashMap<>();
        String postfixUrl = siteUrl.substring(siteUrl.length() - wikipediaPrefixUrl.length() + 1, siteUrl.length());

        try {
            Document document = Jsoup.connect(siteUrl).get();
            Element table = document.select(TABLE).get(0);
            Elements rows = table.select(TR);
            for (int i = 1; i < rows.size(); i++) {
                Element row = rows.get(i);
                if (personalDetails.equals(row.text())) {
                    for (int j = i; j < rows.size(); j++) {
                        String relativeGroup = rows.get(j).select(TH).text();
                        if (relativeGroup.equals(parents))  parseParentsOrChildrenGroup(relativeModelsMap, rows, j, relativeGroup);
                        if (relativeGroup.equals(children)) parseParentsOrChildrenGroup(relativeModelsMap, rows, j, relativeGroup);
                        if (relativeGroup.equals(spouses)) parseSpousesGroup(relativeModelsMap, rows, j, relativeGroup);
                    }
                    break;
                }
            }
        } catch (IOException e) {
           throw new HtmlParsingException("Couldn't parse site: " + siteUrl, e);
        }
        enrichSpouseForSpouse(relativeModelsMap, postfixUrl);
        enrichParentsForChildren(relativeModelsMap, postfixUrl);
        enrichChildrenForParents(relativeModelsMap, postfixUrl);
        return relativeModelsMap;
    }

    private Map<String, List<RelativesModel>> doParseByRelative(String url, String relatives) {
        Map<String, List<RelativesModel>> responses = new HashMap<>();
        try {
            Document document = Jsoup.connect(url).get();
            Element table = document.select(TABLE).get(0);
            Elements rows = table.select(TR);
            for (int i = 1; i < rows.size(); i++) {
                Element row = rows.get(i);
                if (personalDetails.equals(row.text())) {
                    for (int j = i; j < rows.size(); j++) {
                        String relativeGroup = rows.get(j).select(TH).text();
                        if (relativeGroup.equals(relatives)) parseParentsOrChildrenGroup(responses, rows, j, relativeGroup);
                        if (relativeGroup.equals(relatives)) parseSpousesGroup(responses, rows, j, relativeGroup);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            throw new HtmlParsingException("Couldn't parse: " + url, e);
        }
        return responses;
    }


    private void enrichSpouseForSpouse(Map<String, List<RelativesModel>> relativeModelsMap, String postfixUrl) {
        enrichRelativesReferences(relativeModelsMap, postfixUrl, spouses, spouses);
    }


    private void enrichChildrenForParents(Map<String, List<RelativesModel>> relativeModelsMap, String postfix) {
        enrichRelativesReferences(relativeModelsMap, postfix, children, parents);
    }


    private void enrichParentsForChildren(Map<String, List<RelativesModel>> relativeModelsMap, String postfix) {
        enrichRelativesReferences(relativeModelsMap, postfix, parents, children);
    }


    private void parseSpousesGroup(Map<String, List<RelativesModel>> relativeModelsMap,
                                   Elements rows,
                                   int rowIndex,
                                   String relativeGroup) {

        List<RelativesModel> relativesModelList = rows.get(rowIndex).select(A)
            .stream()
            .map(el -> new RelativesModel(el.attr(HREF), el.text()))
            .collect(Collectors.toList());
        relativeModelsMap.put(relativeGroup, relativesModelList);
    }


    private void parseParentsOrChildrenGroup(Map<String, List<RelativesModel>> relativeModelsMap,
                                              Elements rows,
                                              int rowIndex,
                                              String relativeGroup) {

        List<RelativesModel> relativesModelList = rows.get(rowIndex).select(LI)
                .stream()
                .map(el -> new RelativesModel(el.select(A).attr(HREF), el.text()))
                .collect(Collectors.toList());
        relativeModelsMap.put(relativeGroup, relativesModelList);
    }


    private void enrichRelativesReferences(Map<String, List<RelativesModel>> relativeModelsMap,
                                           String postfixUrl,
                                           String firstGroup,
                                           String secondGroup) {

        List<RelativesModel> parsingResultsSpouses=relativeModelsMap.get(firstGroup);
        if (parsingResultsSpouses!=null){
          parsingResultsSpouses
                    .stream()
                    .map(RelativesModel::getUrl)
                    .forEach(url->{
                Map<String, List<RelativesModel>> parsingResultMap =doParseByRelative(wikipediaPrefixUrl + url, secondGroup);
                int x=0;
                if (!parsingResultMap.isEmpty()) {

                    RelativesModel relativesModel = parsingResultMap.get(secondGroup).get(x++);
                    String myUrl = relativesModel.getUrl();
                    boolean areEqual = myUrl.equals(postfixUrl);
                    relativesModel.setReferenced(areEqual);
                }});
        }
    }
}
