import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { isBlank, missingFieldsMessage } from '../../core/utils/form-validation.util';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  username = '';
  password = '';
  readonly loading = signal(false);
  readonly error = signal('');

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router
  ) {
    if (this.authService.isLoggedIn()) {
      this.router.navigateByUrl(this.authService.homeRoute());
    }
  }

  onSubmit(): void {
    this.error.set('');
    const missing = missingFieldsMessage({
      Username: isBlank(this.username),
      Password: isBlank(this.password)
    });
    if (missing) {
      this.error.set(missing);
      return;
    }

    this.loading.set(true);
    this.authService.login(this.username.trim(), this.password).subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigateByUrl(this.authService.homeRoute());
      },
      error: (err) => {
        this.loading.set(false);
        if (err?.status === 0) {
          this.error.set('Cannot reach API. Start backend on port 8080.');
          return;
        }
        this.error.set(err?.error?.message || 'Invalid username or password.');
      }
    });
  }
}
