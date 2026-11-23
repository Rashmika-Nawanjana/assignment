import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { API_BASE_URL } from './api.config';

export type UserRole = 'ADMIN' | 'MANAGER' | 'AGENT';

export interface AuthUser {
  token: string;
  userId: number;
  username: string;
  fullName: string;
  email: string;
  role: UserRole;
}

export interface SignupPayload {
  username: string;
  password: string;
  fullName: string;
  email?: string;
}

const STORAGE_KEY = 'suntravels_auth';

@Injectable({ providedIn: 'root' })
export class AuthService {
  readonly currentUser = signal<AuthUser | null>(this.readStoredUser());

  constructor(
    private readonly http: HttpClient,
    private readonly router: Router
  ) {}

  login(username: string, password: string): Observable<AuthUser> {
    return this.http
      .post<AuthUser>(`${API_BASE_URL}/auth/login`, { username, password })
      .pipe(tap((response) => this.persist(response)));
  }

  signup(payload: SignupPayload): Observable<AuthUser> {
    return this.http
      .post<AuthUser>(`${API_BASE_URL}/auth/signup`, payload)
      .pipe(tap((response) => this.persist(response)));
  }

  logout(): void {
    localStorage.removeItem(STORAGE_KEY);
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return this.currentUser()?.token ?? null;
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  hasRole(...roles: UserRole[]): boolean {
    const role = this.currentUser()?.role;
    return !!role && roles.includes(role);
  }

  canManageContracts(): boolean {
    return this.hasRole('ADMIN', 'MANAGER');
  }

  homeRoute(): string {
    const role = this.currentUser()?.role;
    if (role === 'ADMIN') {
      return '/users';
    }
    if (role === 'MANAGER') {
      return '/contracts';
    }
    return '/search';
  }

  updateStoredProfile( partial: Partial<AuthUser> ): void {
    const current = this.currentUser();
    if (!current) {
      return;
    }
    const updated = { ...current, ...partial };
    this.persist(updated);
  }

  private persist(user: AuthUser): void {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
    this.currentUser.set(user);
  }

  private readStoredUser(): AuthUser | null {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) {
      return null;
    }
    try {
      return JSON.parse(raw) as AuthUser;
    } catch {
      return null;
    }
  }
}
