import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from './api.config';
import { PageResponse } from './page.model';

export interface BookingPayload {
  roomTypeId: number;
  roomNumber: string;
  checkInDate: string;
  numberOfNights: number;
  adults: number;
  guestName: string;
  guestNic: string;
  guestPhone?: string;
  guestEmail?: string;
}

export interface BookingRecord {
  id: number;
  hotelName: string;
  roomTypeName: string;
  roomNumber?: string;
  checkInDate: string;
  numberOfNights: number;
  roomsRequested: number;
  totalAdults: number;
  totalPrice: number;
  guestName: string;
  guestNic: string;
  guestPhone?: string;
  guestEmail?: string;
  status: 'CONFIRMED' | 'CANCELLED';
  bookedBy: string;
  createdAt: string;
}

export interface AvailableRooms {
  roomTypeId: number;
  roomTypeName: string;
  totalRooms: number;
  availableRoomNumbers: string[];
}

@Injectable({ providedIn: 'root' })
export class BookingService {
  constructor(private readonly http: HttpClient) {}

  availableRooms(
    roomTypeId: number,
    checkInDate: string,
    numberOfNights: number
  ): Observable<AvailableRooms> {
    const params = new HttpParams()
      .set('roomTypeId', roomTypeId)
      .set('checkInDate', checkInDate)
      .set('numberOfNights', numberOfNights);
    return this.http.get<AvailableRooms>(`${API_BASE_URL}/bookings/available-rooms`, { params });
  }

  create(payload: BookingPayload): Observable<BookingRecord> {
    return this.http.post<BookingRecord>(`${API_BASE_URL}/bookings`, payload);
  }

  list(q = '', page = 0, size = 8): Observable<PageResponse<BookingRecord>> {
    const params = new HttpParams().set('q', q).set('page', page).set('size', size);
    return this.http.get<PageResponse<BookingRecord>>(`${API_BASE_URL}/bookings`, { params });
  }

  cancel(id: number): Observable<BookingRecord> {
    return this.http.put<BookingRecord>(`${API_BASE_URL}/bookings/${id}/cancel`, {});
  }
}
