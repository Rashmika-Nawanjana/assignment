import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { LoginComponent } from './login.component';
import { AuthService } from '../../core/services/auth.service';

describe('LoginComponent', () => {
  let fixture: ComponentFixture<LoginComponent>;
  let component: LoginComponent;
  let authService: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    authService = jasmine.createSpyObj<AuthService>('AuthService', [
      'isLoggedIn',
      'login',
      'homeRoute'
    ]);
    authService.isLoggedIn.and.returnValue(false);
    authService.homeRoute.and.returnValue('/search');

    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [provideRouter([]), { provide: AuthService, useValue: authService }]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    spyOn(router, 'navigateByUrl');
    fixture.detectChanges();
  });

  it('shows validation when username/password are empty', () => {
    component.onSubmit();
    expect(component.error()).toContain('required');
    expect(authService.login).not.toHaveBeenCalled();
  });

  it('logs in and navigates home on success', () => {
    component.username = 'agent';
    component.password = 'Agent@12345';
    authService.login.and.returnValue(
      of({
        token: 't',
        userId: 1,
        username: 'agent',
        fullName: 'Agent',
        email: '',
        role: 'AGENT'
      })
    );

    component.onSubmit();

    expect(authService.login).toHaveBeenCalledWith('agent', 'Agent@12345');
    expect(router.navigateByUrl).toHaveBeenCalledWith('/search');
    expect(component.loading()).toBeFalse();
  });

  it('shows API error message on failed login', () => {
    component.username = 'agent';
    component.password = 'wrong';
    authService.login.and.returnValue(
      throwError(() => ({ status: 401, error: { message: 'Invalid username or password' } }))
    );

    component.onSubmit();

    expect(component.error()).toBe('Invalid username or password');
    expect(component.loading()).toBeFalse();
  });
});
