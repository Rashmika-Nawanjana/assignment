import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { isStrongPassword, STRONG_PASSWORD_HINT } from '../../core/utils/password.util';
import { isBlank, missingFieldsMessage } from '../../core/utils/form-validation.util';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './signup.component.html',
  styleUrl: './login.component.scss'
})
export class SignupComponent {
  username = '';
  fullName = '';
  email = '';
  password = '';
  confirmPassword = '';
  readonly hint = STRONG_PASSWORD_HINT;
  readonly loading = signal(false);
  readonly error = signal('');

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router
  ) {}

  onSubmit(): void {
    this.error.set('');

    const missing = missingFieldsMessage({
      'Full name': isBlank(this.fullName),
      Username: isBlank(this.username),
      Password: isBlank(this.password),
      'Confirm password': isBlank(this.confirmPassword)
    });
    if (missing) {
      this.error.set(missing);
      return;
    }
    if (!isStrongPassword(this.password)) {
      this.error.set(this.hint);
      return;
    }
    if (this.password !== this.confirmPassword) {
      this.error.set('Passwords do not match.');
      return;
    }

    this.loading.set(true);
    this.authService
      .signup({
        username: this.username.trim(),
        password: this.password,
        fullName: this.fullName.trim(),
        email: this.email.trim() || undefined
      })
      .subscribe({
        next: () => {
          this.loading.set(false);
          this.router.navigateByUrl(this.authService.homeRoute());
        },
        error: (err) => {
          this.loading.set(false);
          this.error.set(err?.error?.message || 'Could not create account.');
        }
      });
  }
}
