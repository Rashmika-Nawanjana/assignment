import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { SearchComponent } from './search.component';
import { SearchApiService, SearchResult } from '../../core/services/search.service';
import { BookingService } from '../../core/services/booking.service';

describe('SearchComponent', () => {
  let fixture: ComponentFixture<SearchComponent>;
  let component: SearchComponent;
  let searchApi: jasmine.SpyObj<SearchApiService>;
  let bookingService: jasmine.SpyObj<BookingService>;

  const sampleResults: SearchResult[] = [
    {
      roomTypeId: 1,
      hotelName: 'Alpha Hotel',
      roomType: 'Standard',
      price: 100,
      availabilityStatus: 'Available'
    },
    {
      roomTypeId: 2,
      hotelName: 'Beta Inn',
      roomType: 'Deluxe',
      price: 50,
      availabilityStatus: 'Not available'
    },
    {
      roomTypeId: 3,
      hotelName: 'Alpha Hotel',
      roomType: 'Suite',
      price: 200,
      availabilityStatus: 'Available'
    }
  ];

  beforeEach(async () => {
    searchApi = jasmine.createSpyObj<SearchApiService>('SearchApiService', ['search']);
    bookingService = jasmine.createSpyObj<BookingService>('BookingService', [
      'availableRooms',
      'create'
    ]);

    await TestBed.configureTestingModule({
      imports: [SearchComponent],
      providers: [
        provideRouter([]),
        { provide: SearchApiService, useValue: searchApi },
        { provide: BookingService, useValue: bookingService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('requires check-in date before searching', () => {
    component.checkInDate = '';
    component.onSubmit();
    expect(component.error()).toContain('Check-in date');
    expect(searchApi.search).not.toHaveBeenCalled();
  });

  it('runs search and stores results', () => {
    component.checkInDate = '2026-06-01';
    component.numberOfNights = 2;
    searchApi.search.and.returnValue(of(sampleResults));

    component.onSubmit();

    expect(searchApi.search).toHaveBeenCalled();
    expect(component.results().length).toBe(3);
    expect(component.searched()).toBeTrue();
    expect(component.loading()).toBeFalse();
  });

  it('filters results by availability and query', () => {
    component.results.set(sampleResults);
    component.onAvailabilityFilter('Available');
    component.onFilterQuery('Alpha');

    expect(component.filteredResults().length).toBe(2);
    expect(component.filteredResults().every((row) => row.availabilityStatus === 'Available')).toBeTrue();
  });

  it('paginates filtered results', () => {
    const many = Array.from({ length: 25 }, (_, i) => ({
      roomTypeId: i + 1,
      hotelName: `Hotel ${i}`,
      roomType: 'Standard',
      price: i,
      availabilityStatus: 'Available' as const
    }));
    component.results.set(many);

    expect(component.totalPages()).toBe(3);
    expect(component.displayedResults().length).toBe(10);
    component.onPage(1);
    expect(component.displayedResults()[0].roomTypeId).toBe(11);
  });

  it('adds and removes rooms in the search form', () => {
    expect(component.rooms.length).toBe(1);
    component.addRoom();
    expect(component.rooms.length).toBe(2);
    component.removeRoom(1);
    expect(component.rooms.length).toBe(1);
  });

  it('requires guest fields and room number when confirming booking', () => {
    component.selected.set(sampleResults[0]);
    component.roomNumber = '';
    component.guestName = '';
    component.guestNic = '';
    component.confirmBooking();
    expect(component.bookingError()).toContain('required');
    expect(bookingService.create).not.toHaveBeenCalled();
  });
});
