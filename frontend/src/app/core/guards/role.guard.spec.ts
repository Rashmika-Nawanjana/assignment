import { TestBed } from '@angular/core/testing';
import { provideRouter, Router, UrlTree } from '@angular/router';
import { roleGuard } from './role.guard';
import { AuthService } from '../services/auth.service';

describe('roleGuard', () => {
  let authService: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(() => {
    authService = jasmine.createSpyObj<AuthService>('AuthService', ['hasRole', 'homeRoute']);
    authService.homeRoute.and.returnValue('/search');
    TestBed.configureTestingModule({
      providers: [provideRouter([]), { provide: AuthService, useValue: authService }]
    });
    router = TestBed.inject(Router);
  });

  it('allows access when role matches', () => {
    authService.hasRole.and.returnValue(true);
    const guard = roleGuard(['ADMIN']);
    const result = TestBed.runInInjectionContext(() => guard({} as never, {} as never));
    expect(result).toBeTrue();
  });

  it('redirects to home when role is not allowed', () => {
    authService.hasRole.and.returnValue(false);
    const guard = roleGuard(['ADMIN']);
    const result = TestBed.runInInjectionContext(() => guard({} as never, {} as never));
    expect(result instanceof UrlTree).toBeTrue();
    expect(router.serializeUrl(result as UrlTree)).toBe('/search');
  });
});
