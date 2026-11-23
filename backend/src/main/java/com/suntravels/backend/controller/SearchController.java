package com.suntravels.backend.controller;

import com.suntravels.backend.dto.SearchRequest;
import com.suntravels.backend.dto.SearchResultResponse;
import com.suntravels.backend.service.SearchService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping( "/api/search" )
public class SearchController
{
    private final SearchService searchService;

    public SearchController( SearchService searchService )
    {
        this.searchService = searchService;
    }

    @PostMapping
    public List<SearchResultResponse> search( @Valid @RequestBody SearchRequest request )
    {
        return searchService.search( request );
    }
}
