import { Component, OnInit, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserAdminPayload, UserRecord, UserService } from '../../core/services/user.service';
import { UserRole } from '../../core/services/auth.service';
import { PaginationComponent } from '../../shared/pagination/pagination.component';
import { isStrongPassword, STRONG_PASSWORD_HINT } from '../../core/utils/password.util';
import { ConfirmDialogService } from '../../shared/confirm-dialog/confirm-dialog.service';
import { isBlank, missingFieldsMessage } from '../../core/utils/form-validation.util';
import { filterRows, sortRows, SortDir, toggleSort } from '../../shared/table/table-query.util';

type UserSortKey = 'fullName' | 'username' | 'role' | 'active';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, FormsModule, PaginationComponent],
  templateUrl: './users.component.html',
  styleUrl: './users.component.scss'
})
export class UsersComponent implements OnInit {
  q = '';
  page = 0;
  size = 8;
  readonly users = signal<UserRecord[]>([]);
  readonly totalPages = signal(0);
  readonly totalElements = signal(0);
  readonly loading = signal(false);
  readonly error = signal('');
  readonly success = signal('');
  readonly hint = STRONG_PASSWORD_HINT;

  readonly tableQuery = signal('');
  readonly roleFilter = signal<'all' | UserRole>('all');
  readonly statusFilter = signal<'all' | 'active' | 'disabled'>('all');
  readonly sortKey = signal<UserSortKey>('fullName');
  readonly sortDir = signal<SortDir>('asc');

  readonly displayedUsers = computed(() => {
    let rows = this.users();
    const role = this.roleFilter();
    const status = this.statusFilter();

    if (role !== 'all') {
      rows = rows.filter((row) => row.role === role);
    }
    if (status === 'active') {
      rows = rows.filter((row) => row.active);
    } else if (status === 'disabled') {
      rows = rows.filter((row) => !row.active);
    }

    rows = filterRows(rows, this.tableQuery(), [
      (row) => row.fullName,
      (row) => row.username,
      (row) => row.email,
      (row) => row.role
    ]);

    const key = this.sortKey();
    return sortRows(
      rows,
      (row) => (key === 'active' ? (row.active ? 1 : 0) : row[key]),
      this.sortDir()
    );
  });

  editingId: number | null = null;
  form: UserAdminPayload = this.blankForm();

  constructor(
    private readonly userService: UserService,
    private readonly confirmDialog: ConfirmDialogService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set('');
    this.userService.list(this.q, this.page, this.size).subscribe({
      next: (res) => {
        this.users.set(res.content);
        this.totalPages.set(res.totalPages);
        this.totalElements.set(res.totalElements);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Could not load users.');
      }
    });
  }

  search(): void {
    this.page = 0;
    this.load();
  }

  onPage(page: number): void {
    this.page = page;
    this.load();
  }

  sortBy(key: UserSortKey): void {
    const next = toggleSort(this.sortKey(), this.sortDir(), key);
    this.sortKey.set(next.key as UserSortKey);
    this.sortDir.set(next.dir);
  }

  sortMark(key: UserSortKey): string {
    if (this.sortKey() !== key) {
      return '↕';
    }
    return this.sortDir() === 'asc' ? '↑' : '↓';
  }

  startCreate(): void {
    this.editingId = null;
    this.form = this.blankForm();
  }

  startEdit(user: UserRecord): void {
    this.editingId = user.id;
    this.form = {
      username: user.username,
      password: '',
      fullName: user.fullName,
      email: user.email,
      role: user.role,
      active: user.active
    };
  }

  save(): void {
    this.error.set('');
    this.success.set('');

    const missing = missingFieldsMessage({
      Username: isBlank(this.form.username),
      'Full name': isBlank(this.form.fullName),
      Role: isBlank(this.form.role),
      ...(this.editingId == null ? { Password: isBlank(this.form.password) } : {})
    });
    if (missing) {
      this.error.set(missing);
      return;
    }

    if (this.editingId == null || this.form.password) {
      if (!this.form.password || !isStrongPassword(this.form.password)) {
        this.error.set(this.hint);
        return;
      }
    }

    const req$ =
      this.editingId == null
        ? this.userService.create(this.form)
        : this.userService.update(this.editingId, this.form);

    req$.subscribe({
      next: () => {
        this.success.set('User saved.');
        this.startCreate();
        this.load();
      },
      error: (err) => this.error.set(err?.error?.message || 'Save failed.')
    });
  }

  async remove(user: UserRecord): Promise<void> {
    const ok = await this.confirmDialog.ask({
      title: 'Delete user',
      message: `Delete user ${user.username}?`,
      confirmText: 'Delete',
      cancelText: 'Keep',
      danger: true
    });
    if (!ok) {
      return;
    }
    this.userService.delete(user.id).subscribe({
      next: () => this.load(),
      error: (err) => this.error.set(err?.error?.message || 'Delete failed.')
    });
  }

  private blankForm(): UserAdminPayload {
    return {
      username: '',
      password: '',
      fullName: '',
      email: '',
      role: 'AGENT',
      active: true
    };
  }

  roles(): UserRole[] {
    return ['ADMIN', 'MANAGER', 'AGENT'];
  }
}
