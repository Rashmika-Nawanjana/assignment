import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideRouter, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { API_BASE_URL } from './api.config';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let router: Router;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    spyOn(router, 'navigate');
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  function flushLogin(role: 'ADMIN' | 'MANAGER' | 'AGENT' = 'ADMIN'): void {
    service.login('user', 'pass').subscribe();
    const req = httpMock.expectOne(`${API_BASE_URL}/auth/login`);
    req.flush({
      token: 'test-token',
      userId: 1,
      username: 'user',
      fullName: 'User',
      email: 'u@x.com',
      role
    });
  }

  it('stores token and user after login', () => {
    flushLogin('ADMIN');
    expect(service.isLoggedIn()).toBeTrue();
    expect(service.getToken()).toBe('test-token');
    expect(service.hasRole('ADMIN')).toBeTrue();
  });

  it('maps home routes by role', () => {
    flushLogin('ADMIN');
    expect(service.homeRoute()).toBe('/users');

    localStorage.clear();
    service.currentUser.set(null);
    flushLogin('MANAGER');
    expect(service.homeRoute()).toBe('/contracts');

    localStorage.clear();
    service.currentUser.set(null);
    flushLogin('AGENT');
    expect(service.homeRoute()).toBe('/search');
  });

  it('clears session on logout', () => {
    flushLogin('AGENT');
    service.logout();
    expect(service.isLoggedIn()).toBeFalse();
    expect(service.getToken()).toBeNull();
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('persists signup response', () => {
    service.signup({ username: 'new', password: 'Agent@12345', fullName: 'New' }).subscribe();
    const req = httpMock.expectOne(`${API_BASE_URL}/auth/signup`);
    expect(req.request.method).toBe('POST');
    req.flush({
      token: 'signup-token',
      userId: 9,
      username: 'new',
      fullName: 'New',
      email: '',
      role: 'AGENT'
    });
    expect(service.getToken()).toBe('signup-token');
    expect(service.hasRole('AGENT')).toBeTrue();
  });
});
