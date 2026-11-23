import { Component, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { SearchApiService, SearchResult } from '../../core/services/search.service';
import { BookingService } from '../../core/services/booking.service';
import { isBlank, missingFieldsMessage } from '../../core/utils/form-validation.util';
import { filterRows, sortRows, SortDir, toggleSort } from '../../shared/table/table-query.util';
import { PaginationComponent } from '../../shared/pagination/pagination.component';

type SearchSortKey = 'hotelName' | 'roomType' | 'price' | 'availabilityStatus';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [CommonModule, FormsModule, PaginationComponent],
  templateUrl: './search.component.html',
  styleUrl: './search.component.scss'
})
export class SearchComponent {
  checkInDate = '';
  numberOfNights = 1;
  rooms: { adults: number }[] = [{ adults: 2 }];

  guestName = '';
  guestNic = '';
  guestPhone = '';
  guestEmail = '';
  adultsForBooking = 1;
  roomNumber = '';
  readonly pageSize = 10;

  readonly resultQuery = signal('');
  readonly availabilityFilter = signal<'all' | 'Available' | 'Not available'>('all');
  readonly sortKey = signal<SearchSortKey>('price');
  readonly sortDir = signal<SortDir>('asc');
  readonly page = signal(0);

  readonly selected = signal<SearchResult | null>(null);
  readonly availableRoomNumbers = signal<string[]>([]);
  readonly loadingRooms = signal(false);
  readonly loading = signal(false);
  readonly booking = signal(false);
  readonly searched = signal(false);
  readonly error = signal('');
  readonly bookingError = signal('');
  readonly success = signal('');
  readonly results = signal<SearchResult[]>([]);
  readonly lastBookedRoomTypeId = signal<number | null>(null);

  readonly filteredResults = computed(() => {
    let rows = this.results();
    const availability = this.availabilityFilter();

    if (availability !== 'all') {
      rows = rows.filter((row) => row.availabilityStatus === availability);
    }

    rows = filterRows(rows, this.resultQuery(), [
      (row) => row.hotelName,
      (row) => row.roomType,
      (row) => row.availabilityStatus,
      (row) => row.price
    ]);

    const key = this.sortKey();
    return sortRows(rows, (row) => row[key], this.sortDir());
  });

  readonly totalPages = computed(() =>
    Math.max(1, Math.ceil(this.filteredResults().length / this.pageSize))
  );

  readonly displayedResults = computed(() => {
    const rows = this.filteredResults();
    const start = this.page() * this.pageSize;
    return rows.slice(start, start + this.pageSize);
  });

  constructor(
    private readonly searchApi: SearchApiService,
    private readonly bookingService: BookingService,
    private readonly router: Router
  ) {}

  addRoom(): void {
    this.rooms.push({ adults: 1 });
  }

  removeRoom(index: number): void {
    if (this.rooms.length > 1) {
      this.rooms.splice(index, 1);
    }
  }

  onSubmit(): void {
    this.error.set('');
    this.success.set('');
    this.lastBookedRoomTypeId.set(null);
    this.closeBookingForm();
    this.runSearch();
  }

  openBookingForm(row: SearchResult): void {
    this.error.set('');
    this.bookingError.set('');
    this.success.set('');
    if (row.availabilityStatus !== 'Available') {
      this.error.set('Only Available room types can be booked.');
      return;
    }
    this.selected.set(row);
    this.adultsForBooking = this.rooms[0]?.adults || 1;
    this.guestName = '';
    this.guestNic = '';
    this.guestPhone = '';
    this.guestEmail = '';
    this.roomNumber = '';
    this.availableRoomNumbers.set([]);
    this.loadAvailableRooms(row.roomTypeId);
    setTimeout(() => {
      document.getElementById('booking-panel')?.scrollIntoView({
        behavior: 'smooth',
        block: 'start'
      });
    });
  }

  closeBookingForm(): void {
    this.selected.set(null);
    this.availableRoomNumbers.set([]);
    this.roomNumber = '';
    this.bookingError.set('');
  }

  confirmBooking(): void {
    const row = this.selected();
    if (!row) {
      return;
    }
    this.bookingError.set('');
    this.success.set('');

    const missing = missingFieldsMessage({
      'Room number': isBlank(this.roomNumber),
      'Guest full name': isBlank(this.guestName),
      'NIC / ID number': isBlank(this.guestNic),
      'Adults in this room': !(Number(this.adultsForBooking) >= 1)
    });
    if (missing) {
      this.bookingError.set(missing);
      return;
    }

    this.booking.set(true);
    this.bookingService
      .create({
        roomTypeId: row.roomTypeId,
        roomNumber: this.roomNumber.trim(),
        checkInDate: this.checkInDate,
        numberOfNights: Number(this.numberOfNights),
        adults: Number(this.adultsForBooking),
        guestName: this.guestName.trim(),
        guestNic: this.guestNic.trim(),
        guestPhone: this.guestPhone.trim() || undefined,
        guestEmail: this.guestEmail.trim() || undefined
      })
      .subscribe({
        next: (created) => {
          this.booking.set(false);
          this.lastBookedRoomTypeId.set(row.roomTypeId);
          this.success.set(
            `Booking #${created.id} confirmed — Room ${created.roomNumber} for ${created.guestName} at ${created.hotelName}.`
          );
          this.closeBookingForm();
          this.runSearch(false);
        },
        error: (err) => {
          this.booking.set(false);
          this.bookingError.set(err?.error?.message || 'Booking failed.');
          this.loadAvailableRooms(row.roomTypeId);
          setTimeout(() => {
            document.getElementById('booking-error')?.scrollIntoView({
              behavior: 'smooth',
              block: 'nearest'
            });
          });
        }
      });
  }

  private loadAvailableRooms(roomTypeId: number): void {
    if (!this.checkInDate || Number(this.numberOfNights) < 1) {
      return;
    }
    this.loadingRooms.set(true);
    this.bookingService
      .availableRooms(roomTypeId, this.checkInDate, Number(this.numberOfNights))
      .subscribe({
        next: (res) => {
          this.availableRoomNumbers.set(res.availableRoomNumbers);
          this.loadingRooms.set(false);
          if (res.availableRoomNumbers.length === 0) {
            this.bookingError.set('No free room numbers left for these dates.');
          } else if (!res.availableRoomNumbers.includes(this.roomNumber)) {
            this.roomNumber = res.availableRoomNumbers[0];
          }
        },
        error: (err) => {
          this.loadingRooms.set(false);
          this.availableRoomNumbers.set([]);
          this.bookingError.set(err?.error?.message || 'Could not load available room numbers.');
        }
      });
  }

  goToBookings(): void {
    this.router.navigate(['/bookings']);
  }

  sortBy(key: SearchSortKey): void {
    const next = toggleSort(this.sortKey(), this.sortDir(), key);
    this.sortKey.set(next.key as SearchSortKey);
    this.sortDir.set(next.dir);
    this.page.set(0);
  }

  sortMark(key: SearchSortKey): string {
    if (this.sortKey() !== key) {
      return '↕';
    }
    return this.sortDir() === 'asc' ? '↑' : '↓';
  }

  onFilterQuery(value: string): void {
    this.resultQuery.set(value);
    this.page.set(0);
  }

  onAvailabilityFilter(value: 'all' | 'Available' | 'Not available'): void {
    this.availabilityFilter.set(value);
    this.page.set(0);
  }

  onPage(page: number): void {
    this.page.set(page);
  }

  private runSearch(clearMessages = true): void {
    if (clearMessages) {
      this.error.set('');
      this.success.set('');
    }

    const missing = missingFieldsMessage({
      'Check-in date': isBlank(this.checkInDate),
      'Number of nights': !(Number(this.numberOfNights) >= 1),
      'Room adults': this.rooms.some((room) => !(Number(room.adults) >= 1))
    });
    if (missing) {
      this.error.set(missing);
      this.loading.set(false);
      return;
    }

    this.loading.set(true);
    this.searched.set(true);

    this.searchApi
      .search({
        checkInDate: this.checkInDate,
        numberOfNights: Number(this.numberOfNights),
        rooms: this.rooms.map((room) => ({ adults: Number(room.adults) }))
      })
      .subscribe({
        next: (data) => {
          this.results.set([...data]);
          this.page.set(0);
          this.loading.set(false);
          if (data.length === 0 && clearMessages) {
            this.success.set('');
          }
        },
        error: (err) => {
          this.loading.set(false);
          this.results.set([]);
          this.page.set(0);
          this.error.set(
            err?.error?.message || 'Search failed. Check dates and that the backend is running.'
          );
        }
      });
  }
}
