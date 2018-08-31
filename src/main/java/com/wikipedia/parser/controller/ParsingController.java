package com.wikipedia.parser.controller;

import com.wikipedia.parser.model.RelativesModel;
import com.wikipedia.parser.service.ParsingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
public class ParsingController {

    private final ParsingService parsingService;

    @Autowired
    public ParsingController(ParsingService parsingService) {
        this.parsingService = parsingService;
    }

    @CrossOrigin("*")
    @GetMapping(value = "/parse")
    public Map<String,List<RelativesModel>> siteParse(@RequestParam(value = "parseUrl") String siteUrl) {
        return parsingService.parseSite(siteUrl);
    }
}
