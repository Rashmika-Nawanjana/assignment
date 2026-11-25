import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { SignupComponent } from './signup.component';
import { AuthService } from '../../core/services/auth.service';

describe('SignupComponent', () => {
  let fixture: ComponentFixture<SignupComponent>;
  let component: SignupComponent;
  let authService: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    authService = jasmine.createSpyObj<AuthService>('AuthService', ['signup', 'homeRoute']);
    authService.homeRoute.and.returnValue('/search');

    await TestBed.configureTestingModule({
      imports: [SignupComponent],
      providers: [provideRouter([]), { provide: AuthService, useValue: authService }]
    }).compileComponents();

    fixture = TestBed.createComponent(SignupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('requires core fields', () => {
    component.onSubmit();
    expect(component.error()).toContain('required');
    expect(authService.signup).not.toHaveBeenCalled();
  });

  it('rejects weak passwords', () => {
    component.fullName = 'Test User';
    component.username = 'tester';
    component.password = 'weak';
    component.confirmPassword = 'weak';
    component.onSubmit();
    expect(component.error().toLowerCase()).toContain('min 8');
    expect(authService.signup).not.toHaveBeenCalled();
  });

  it('rejects mismatched passwords', () => {
    component.fullName = 'Test User';
    component.username = 'tester';
    component.password = 'Agent@12345';
    component.confirmPassword = 'Agent@99999';
    component.onSubmit();
    expect(component.error()).toBe('Passwords do not match.');
  });

  it('signs up with a strong password', () => {
    component.fullName = 'Test User';
    component.username = 'tester';
    component.password = 'Agent@12345';
    component.confirmPassword = 'Agent@12345';
    authService.signup.and.returnValue(
      of({
        token: 't',
        userId: 2,
        username: 'tester',
        fullName: 'Test User',
        email: '',
        role: 'AGENT'
      })
    );

    component.onSubmit();

    expect(authService.signup).toHaveBeenCalled();
    expect(component.loading()).toBeFalse();
  });
});
