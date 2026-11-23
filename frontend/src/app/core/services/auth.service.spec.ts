import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { AuthService } from './auth.service';
import { API_BASE_URL } from './api.config';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('stores token and user after login', () => {
    service.login('admin', 'Admin@12345').subscribe((response) => {
      expect(response.role).toBe('ADMIN');
      expect(service.isLoggedIn()).toBeTrue();
      expect(service.getToken()).toBe('test-token');
    });

    const req = httpMock.expectOne(`${API_BASE_URL}/auth/login`);
    expect(req.request.method).toBe('POST');
    req.flush({
      token: 'test-token',
      userId: 1,
      username: 'admin',
      fullName: 'System Admin',
      email: 'admin@suntravels.local',
      role: 'ADMIN'
    });
  });
});
