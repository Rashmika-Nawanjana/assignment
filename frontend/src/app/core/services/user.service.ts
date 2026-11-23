import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from './api.config';
import { PageResponse } from './page.model';
import { UserRole } from './auth.service';

export interface UserRecord {
  id: number;
  username: string;
  fullName: string;
  email: string;
  role: UserRole;
  active: boolean;
}

export interface UserAdminPayload {
  username: string;
  password?: string;
  fullName: string;
  email?: string;
  role: UserRole;
  active: boolean;
}

@Injectable({ providedIn: 'root' })
export class UserService {
  constructor(private readonly http: HttpClient) {}

  list(q = '', page = 0, size = 8): Observable<PageResponse<UserRecord>> {
    const params = new HttpParams().set('q', q).set('page', page).set('size', size);
    return this.http.get<PageResponse<UserRecord>>(`${API_BASE_URL}/users`, { params });
  }

  create(payload: UserAdminPayload): Observable<UserRecord> {
    return this.http.post<UserRecord>(`${API_BASE_URL}/users`, payload);
  }

  update(id: number, payload: UserAdminPayload): Observable<UserRecord> {
    return this.http.put<UserRecord>(`${API_BASE_URL}/users/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${API_BASE_URL}/users/${id}`);
  }

  getProfile(): Observable<UserRecord> {
    return this.http.get<UserRecord>(`${API_BASE_URL}/profile`);
  }

  updateProfile(payload: { fullName: string; email?: string }): Observable<UserRecord> {
    return this.http.put<UserRecord>(`${API_BASE_URL}/profile`, payload);
  }

  changePassword(payload: { currentPassword: string; newPassword: string }): Observable<void> {
    return this.http.put<void>(`${API_BASE_URL}/profile/password`, payload);
  }
}
