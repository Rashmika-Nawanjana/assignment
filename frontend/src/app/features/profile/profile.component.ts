import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { isStrongPassword, STRONG_PASSWORD_HINT } from '../../core/utils/password.util';
import { isBlank, missingFieldsMessage } from '../../core/utils/form-validation.util';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnInit {
  fullName = '';
  email = '';
  currentPassword = '';
  newPassword = '';
  readonly hint = STRONG_PASSWORD_HINT;
  readonly message = signal('');
  readonly error = signal('');
  readonly saving = signal(false);

  constructor(
    readonly auth: AuthService,
    private readonly userService: UserService
  ) {}

  ngOnInit(): void {
    this.userService.getProfile().subscribe({
      next: (profile) => {
        this.fullName = profile.fullName || '';
        this.email = profile.email || '';
      }
    });
  }

  saveProfile(): void {
    this.error.set('');
    this.message.set('');

    const missing = missingFieldsMessage({
      'Full name': isBlank(this.fullName)
    });
    if (missing) {
      this.error.set(missing);
      return;
    }

    this.saving.set(true);
    this.userService.updateProfile({ fullName: this.fullName, email: this.email }).subscribe({
      next: (profile) => {
        this.saving.set(false);
        this.message.set('Profile updated.');
        this.auth.updateStoredProfile({
          fullName: profile.fullName,
          email: profile.email
        });
      },
      error: (err) => {
        this.saving.set(false);
        this.error.set(err?.error?.message || 'Could not update profile.');
      }
    });
  }

  changePassword(): void {
    this.error.set('');
    this.message.set('');

    const missing = missingFieldsMessage({
      'Current password': isBlank(this.currentPassword),
      'New password': isBlank(this.newPassword)
    });
    if (missing) {
      this.error.set(missing);
      return;
    }
    if (!isStrongPassword(this.newPassword)) {
      this.error.set(this.hint);
      return;
    }
    this.saving.set(true);
    this.userService
      .changePassword({
        currentPassword: this.currentPassword,
        newPassword: this.newPassword
      })
      .subscribe({
        next: () => {
          this.saving.set(false);
          this.message.set('Password changed.');
          this.currentPassword = '';
          this.newPassword = '';
        },
        error: (err) => {
          this.saving.set(false);
          this.error.set(err?.error?.message || 'Could not change password.');
        }
      });
  }
}
