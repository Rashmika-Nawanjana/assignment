import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';
import { LoginComponent } from './features/auth/login.component';
import { SignupComponent } from './features/auth/signup.component';
import { ContractsComponent } from './features/contracts/contracts.component';
import { SearchComponent } from './features/search/search.component';
import { UsersComponent } from './features/users/users.component';
import { ProfileComponent } from './features/profile/profile.component';
import { BookingsComponent } from './features/bookings/bookings.component';
import { ShellComponent } from './layout/shell.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent },
  {
    path: '',
    component: ShellComponent,
    canActivate: [authGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'search' },
      {
        path: 'users',
        component: UsersComponent,
        canActivate: [roleGuard(['ADMIN'])]
      },
      {
        path: 'contracts',
        component: ContractsComponent,
        canActivate: [roleGuard(['ADMIN', 'MANAGER', 'AGENT'])]
      },
      {
        path: 'search',
        component: SearchComponent,
        canActivate: [roleGuard(['ADMIN', 'MANAGER', 'AGENT'])]
      },
      {
        path: 'bookings',
        component: BookingsComponent,
        canActivate: [roleGuard(['ADMIN', 'MANAGER', 'AGENT'])]
      },
      {
        path: 'profile',
        component: ProfileComponent
      }
    ]
  },
  { path: '**', redirectTo: 'login' }
];
