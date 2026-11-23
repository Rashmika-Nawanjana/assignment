package com.suntravels.backend.service;

import com.suntravels.backend.dto.ContractRequest;
import com.suntravels.backend.dto.ContractResponse;
import com.suntravels.backend.dto.PageResponse;
import com.suntravels.backend.dto.RoomTypeRequest;
import com.suntravels.backend.dto.RoomTypeResponse;
import com.suntravels.backend.entity.Contract;
import com.suntravels.backend.entity.Hotel;
import com.suntravels.backend.entity.RoomType;
import com.suntravels.backend.repository.ContractRepository;
import com.suntravels.backend.repository.HotelRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ContractService
{
    private static final BigDecimal DEFAULT_MARKUP = new BigDecimal( "1.150" );

    private final ContractRepository contractRepository;
    private final HotelRepository hotelRepository;

    public ContractService( ContractRepository contractRepository,
                            HotelRepository hotelRepository )
    {
        this.contractRepository = contractRepository;
        this.hotelRepository = hotelRepository;
    }

    @Transactional
    public ContractResponse createContract( ContractRequest request )
    {
        validateDates( request );
        Hotel hotel = findOrCreateHotel( request.getHotelName() );

        Contract contract = new Contract();
        contract.setHotel( hotel );
        applyContractFields( contract, request );
        Contract saved = contractRepository.save( contract );
        return toResponse( saved );
    }

    @Transactional
    public ContractResponse updateContract( Long id, ContractRequest request )
    {
        validateDates( request );
        Contract contract = contractRepository.findById( id )
                .orElseThrow( () -> new ResponseStatusException( NOT_FOUND, "Contract not found" ) );

        Hotel hotel = findOrCreateHotel( request.getHotelName() );
        contract.setHotel( hotel );
        contract.getRoomTypes().clear();
        applyContractFields( contract, request );
        return toResponse( contractRepository.save( contract ) );
    }

    @Transactional
    public void deleteContract( Long id )
    {
        if( !contractRepository.existsById( id ) )
        {
            throw new ResponseStatusException( NOT_FOUND, "Contract not found" );
        }
        contractRepository.deleteById( id );
    }

    @Transactional( readOnly = true )
    public PageResponse<ContractResponse> listContracts( String query, int page, int size )
    {
        String q = query == null ? "" : query.trim();
        PageRequest pageable = PageRequest.of( page, size, Sort.by( "id" ).descending() );
        Page<Contract> result = contractRepository.searchByHotelName( q, pageable );
        return new PageResponse<>( result.map( this::toResponse ) );
    }

    @Transactional( readOnly = true )
    public ContractResponse getContract( Long id )
    {
        Contract contract = contractRepository.findById( id )
                .orElseThrow( () -> new ResponseStatusException( NOT_FOUND, "Contract not found" ) );
        return toResponse( contract );
    }

    private void validateDates( ContractRequest request )
    {
        if( request.getValidTo().isBefore( request.getValidFrom() ) )
        {
            throw new ResponseStatusException( BAD_REQUEST, "validTo must be on or after validFrom" );
        }
    }

    private Hotel findOrCreateHotel( String hotelName )
    {
        return hotelRepository.findByNameIgnoreCase( hotelName.trim() )
                .orElseGet( () -> {
                    Hotel created = new Hotel();
                    created.setName( hotelName.trim() );
                    return hotelRepository.save( created );
                } );
    }

    private void applyContractFields( Contract contract, ContractRequest request )
    {
        contract.setValidFrom( request.getValidFrom() );
        contract.setValidTo( request.getValidTo() );
        contract.setMarkup( request.getMarkup() == null ? DEFAULT_MARKUP : request.getMarkup() );

        List<RoomType> roomTypes = new ArrayList<>();
        for( RoomTypeRequest roomRequest : request.getRoomTypes() )
        {
            RoomType roomType = new RoomType();
            roomType.setContract( contract );
            roomType.setName( roomRequest.getName().trim() );
            roomType.setPricePerPersonPerNight( roomRequest.getPricePerPersonPerNight() );
            roomType.setNumberOfRooms( roomRequest.getNumberOfRooms() );
            roomType.setMaxAdults( roomRequest.getMaxAdults() );
            roomTypes.add( roomType );
        }
        contract.getRoomTypes().addAll( roomTypes );
    }

    private ContractResponse toResponse( Contract contract )
    {
        ContractResponse response = new ContractResponse();
        response.setId( contract.getId() );
        response.setHotelName( contract.getHotel().getName() );
        response.setValidFrom( contract.getValidFrom() );
        response.setValidTo( contract.getValidTo() );
        response.setMarkup( contract.getMarkup() );

        List<RoomTypeResponse> rooms = new ArrayList<>();
        for( RoomType roomType : contract.getRoomTypes() )
        {
            RoomTypeResponse roomResponse = new RoomTypeResponse();
            roomResponse.setId( roomType.getId() );
            roomResponse.setName( roomType.getName() );
            roomResponse.setPricePerPersonPerNight( roomType.getPricePerPersonPerNight() );
            roomResponse.setNumberOfRooms( roomType.getNumberOfRooms() );
            roomResponse.setMaxAdults( roomType.getMaxAdults() );
            rooms.add( roomResponse );
        }
        response.setRoomTypes( rooms );
        return response;
    }
}
