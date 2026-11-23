package com.suntravels.backend.controller;

import com.suntravels.backend.dto.ContractRequest;
import com.suntravels.backend.dto.ContractResponse;
import com.suntravels.backend.dto.PageResponse;
import com.suntravels.backend.service.ContractService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/api/contracts" )
public class ContractController
{
    private final ContractService contractService;

    public ContractController( ContractService contractService )
    {
        this.contractService = contractService;
    }

    @PostMapping
    @ResponseStatus( HttpStatus.CREATED )
    public ContractResponse create( @Valid @RequestBody ContractRequest request )
    {
        return contractService.createContract( request );
    }

    @PutMapping( "/{id}" )
    public ContractResponse update( @PathVariable Long id,
                                    @Valid @RequestBody ContractRequest request )
    {
        return contractService.updateContract( id, request );
    }

    @DeleteMapping( "/{id}" )
    @ResponseStatus( HttpStatus.NO_CONTENT )
    public void delete( @PathVariable Long id )
    {
        contractService.deleteContract( id );
    }

    @GetMapping
    public PageResponse<ContractResponse> list( @RequestParam( defaultValue = "" ) String q,
                                                @RequestParam( defaultValue = "0" ) int page,
                                                @RequestParam( defaultValue = "8" ) int size )
    {
        return contractService.listContracts( q, page, size );
    }

    @GetMapping( "/{id}" )
    public ContractResponse getById( @PathVariable Long id )
    {
        return contractService.getContract( id );
    }
}
