import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from './api.config';
import { PageResponse } from './page.model';

export interface RoomTypePayload {
  name: string;
  pricePerPersonPerNight: number;
  numberOfRooms: number;
  maxAdults: number;
}

export interface ContractPayload {
  hotelName: string;
  validFrom: string;
  validTo: string;
  markup: number;
  roomTypes: RoomTypePayload[];
}

export interface ContractResponse extends ContractPayload {
  id: number;
  roomTypes: (RoomTypePayload & { id?: number })[];
}

@Injectable({ providedIn: 'root' })
export class ContractService {
  constructor(private readonly http: HttpClient) {}

  list(q = '', page = 0, size = 8): Observable<PageResponse<ContractResponse>> {
    const params = new HttpParams()
      .set('q', q)
      .set('page', page)
      .set('size', size);
    return this.http.get<PageResponse<ContractResponse>>(`${API_BASE_URL}/contracts`, { params });
  }

  create(payload: ContractPayload): Observable<ContractResponse> {
    return this.http.post<ContractResponse>(`${API_BASE_URL}/contracts`, payload);
  }

  update(id: number, payload: ContractPayload): Observable<ContractResponse> {
    return this.http.put<ContractResponse>(`${API_BASE_URL}/contracts/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${API_BASE_URL}/contracts/${id}`);
  }
}
