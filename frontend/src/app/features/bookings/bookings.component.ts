import { Component, OnInit, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BookingRecord, BookingService } from '../../core/services/booking.service';
import { PaginationComponent } from '../../shared/pagination/pagination.component';
import { ConfirmDialogService } from '../../shared/confirm-dialog/confirm-dialog.service';
import { filterRows, sortRows, SortDir, toggleSort } from '../../shared/table/table-query.util';

type BookingSortKey = 'id' | 'guestName' | 'hotelName' | 'checkInDate' | 'totalPrice' | 'status';

@Component({
  selector: 'app-bookings',
  standalone: true,
  imports: [CommonModule, FormsModule, PaginationComponent],
  templateUrl: './bookings.component.html',
  styleUrl: './bookings.component.scss'
})
export class BookingsComponent implements OnInit {
  q = '';
  page = 0;
  size = 8;
  readonly bookings = signal<BookingRecord[]>([]);
  readonly totalPages = signal(0);
  readonly totalElements = signal(0);
  readonly loading = signal(false);
  readonly error = signal('');
  readonly success = signal('');

  readonly tableQuery = signal('');
  readonly statusFilter = signal<'all' | 'CONFIRMED' | 'CANCELLED'>('all');
  readonly sortKey = signal<BookingSortKey>('id');
  readonly sortDir = signal<SortDir>('desc');

  readonly displayedBookings = computed(() => {
    let rows = this.bookings();
    const status = this.statusFilter();
    if (status !== 'all') {
      rows = rows.filter((row) => row.status === status);
    }

    rows = filterRows(rows, this.tableQuery(), [
      (row) => row.id,
      (row) => row.guestName,
      (row) => row.guestNic,
      (row) => row.hotelName,
      (row) => row.roomTypeName,
      (row) => row.roomNumber,
      (row) => row.bookedBy,
      (row) => row.status
    ]);

    const key = this.sortKey();
    return sortRows(rows, (row) => row[key], this.sortDir());
  });

  constructor(
    private readonly bookingService: BookingService,
    private readonly confirmDialog: ConfirmDialogService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set('');
    this.bookingService.list(this.q, this.page, this.size).subscribe({
      next: (res) => {
        this.bookings.set(res.content);
        this.totalPages.set(res.totalPages);
        this.totalElements.set(res.totalElements);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Could not load bookings.');
      }
    });
  }

  search(): void {
    this.page = 0;
    this.load();
  }

  onPage(page: number): void {
    this.page = page;
    this.load();
  }

  sortBy(key: BookingSortKey): void {
    const next = toggleSort(this.sortKey(), this.sortDir(), key);
    this.sortKey.set(next.key as BookingSortKey);
    this.sortDir.set(next.dir);
  }

  sortMark(key: BookingSortKey): string {
    if (this.sortKey() !== key) {
      return '↕';
    }
    return this.sortDir() === 'asc' ? '↑' : '↓';
  }

  async cancel(booking: BookingRecord): Promise<void> {
    if (booking.status === 'CANCELLED') {
      return;
    }

    const ok = await this.confirmDialog.ask({
      title: 'Cancel booking',
      message: `Cancel booking #${booking.id} for ${booking.guestName}?`,
      confirmText: 'Cancel booking',
      cancelText: 'Keep booking',
      danger: true
    });
    if (!ok) {
      return;
    }

    this.bookingService.cancel(booking.id).subscribe({
      next: () => {
        this.success.set(`Booking #${booking.id} cancelled.`);
        this.load();
      },
      error: (err) => this.error.set(err?.error?.message || 'Cancel failed.')
    });
  }
}
