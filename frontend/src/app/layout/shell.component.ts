import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from '../core/services/auth.service';
import { ConfirmDialogComponent } from '../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive, ConfirmDialogComponent],
  templateUrl: './shell.component.html',
  styleUrl: './shell.component.scss'
})
export class ShellComponent {
  pageTitle = 'Workspace';

  constructor(
    readonly auth: AuthService,
    private readonly router: Router
  ) {
    this.router.events
      .pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
      .subscribe((event) => {
        this.pageTitle = this.titleFor(event.urlAfterRedirects);
      });
    this.pageTitle = this.titleFor(this.router.url);
  }

  logout(): void {
    this.auth.logout();
  }

  initials(): string {
    const name = this.auth.currentUser()?.fullName || this.auth.currentUser()?.username || 'ST';
    return name
      .split(' ')
      .filter(Boolean)
      .slice(0, 2)
      .map((part) => part[0]?.toUpperCase() || '')
      .join('');
  }

  private titleFor(url: string): string {
    if (url.includes('/users')) return 'Users';
    if (url.includes('/contracts')) return 'Contracts';
    if (url.includes('/search')) return 'Room search';
    if (url.includes('/bookings')) return 'Bookings';
    if (url.includes('/profile')) return 'Profile';
    return 'Workspace';
  }
}
