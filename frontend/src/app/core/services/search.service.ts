import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from './api.config';

export interface SearchPayload {
  checkInDate: string;
  numberOfNights: number;
  rooms: { adults: number }[];
}

export interface SearchResult {
  roomTypeId: number;
  hotelName: string;
  roomType: string;
  price: number;
  availabilityStatus: string;
}

@Injectable({ providedIn: 'root' })
export class SearchApiService {
  constructor(private readonly http: HttpClient) {}

  search(payload: SearchPayload): Observable<SearchResult[]> {
    return this.http.post<SearchResult[]>(`${API_BASE_URL}/search`, payload);
  }
}
