import { Component, OnInit, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import {
  ContractPayload,
  ContractResponse,
  ContractService,
  RoomTypePayload
} from '../../core/services/contract.service';
import { PaginationComponent } from '../../shared/pagination/pagination.component';
import { ConfirmDialogService } from '../../shared/confirm-dialog/confirm-dialog.service';
import { isBlank, missingFieldsMessage } from '../../core/utils/form-validation.util';
import { filterRows, sortRows, SortDir, toggleSort } from '../../shared/table/table-query.util';

type ContractSortKey = 'hotelName' | 'validFrom' | 'validTo' | 'markup';

@Component({
  selector: 'app-contracts',
  standalone: true,
  imports: [CommonModule, FormsModule, PaginationComponent],
  templateUrl: './contracts.component.html',
  styleUrl: './contracts.component.scss'
})
export class ContractsComponent implements OnInit {
  q = '';
  page = 0;
  size = 8;
  readonly contracts = signal<ContractResponse[]>([]);
  readonly totalPages = signal(0);
  readonly totalElements = signal(0);
  readonly loading = signal(false);
  readonly saving = signal(false);
  readonly error = signal('');
  readonly success = signal('');

  readonly tableQuery = signal('');
  readonly sortKey = signal<ContractSortKey>('hotelName');
  readonly sortDir = signal<SortDir>('asc');

  readonly displayedContracts = computed(() => {
    const filtered = filterRows(this.contracts(), this.tableQuery(), [
      (row) => row.hotelName,
      (row) => row.validFrom,
      (row) => row.validTo,
      (row) => row.markup,
      (row) => row.roomTypes.map((room) => room.name).join(' ')
    ]);
    const key = this.sortKey();
    return sortRows(filtered, (row) => row[key], this.sortDir());
  });

  editingId: number | null = null;
  hotelName = '';
  validFrom = '';
  validTo = '';
  markup = 1.15;
  roomTypes: RoomTypePayload[] = [this.blankRoom()];

  constructor(
    private readonly contractService: ContractService,
    readonly auth: AuthService,
    private readonly confirmDialog: ConfirmDialogService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  get canEdit(): boolean {
    return this.auth.canManageContracts();
  }

  load(): void {
    this.loading.set(true);
    this.error.set('');
    this.contractService.list(this.q, this.page, this.size).subscribe({
      next: (res) => {
        this.contracts.set(res.content);
        this.totalPages.set(res.totalPages);
        this.totalElements.set(res.totalElements);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Could not load contracts.');
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

  sortBy(key: ContractSortKey): void {
    const next = toggleSort(this.sortKey(), this.sortDir(), key);
    this.sortKey.set(next.key as ContractSortKey);
    this.sortDir.set(next.dir);
  }

  sortMark(key: ContractSortKey): string {
    if (this.sortKey() !== key) {
      return '↕';
    }
    return this.sortDir() === 'asc' ? '↑' : '↓';
  }

  addRoomRow(): void {
    this.roomTypes.push(this.blankRoom());
  }

  removeRoomRow(index: number): void {
    if (this.roomTypes.length > 1) {
      this.roomTypes.splice(index, 1);
    }
  }

  startEdit(contract: ContractResponse): void {
    this.editingId = contract.id;
    this.hotelName = contract.hotelName;
    this.validFrom = contract.validFrom;
    this.validTo = contract.validTo;
    this.markup = Number(contract.markup);
    this.roomTypes = contract.roomTypes.map((room) => ({
      name: room.name,
      pricePerPersonPerNight: Number(room.pricePerPersonPerNight),
      numberOfRooms: Number(room.numberOfRooms),
      maxAdults: Number(room.maxAdults)
    }));
  }

  clearForm(): void {
    this.editingId = null;
    this.hotelName = '';
    this.validFrom = '';
    this.validTo = '';
    this.markup = 1.15;
    this.roomTypes = [this.blankRoom()];
  }

  onSubmit(): void {
    this.error.set('');
    this.success.set('');

    const missingRoomName = this.roomTypes.some((room) => isBlank(room.name));
    const missingRoomPrice = this.roomTypes.some(
      (room) => !(Number(room.pricePerPersonPerNight) > 0)
    );
    const missingRoomCounts = this.roomTypes.some(
      (room) => !(Number(room.numberOfRooms) >= 1) || !(Number(room.maxAdults) >= 1)
    );

    const missing = missingFieldsMessage({
      'Hotel name': isBlank(this.hotelName),
      'Valid from': isBlank(this.validFrom),
      'Valid to': isBlank(this.validTo),
      Markup: !(Number(this.markup) >= 1),
      'Room type name': missingRoomName,
      'Price / person / night': missingRoomPrice,
      'No. of rooms / Max adults': missingRoomCounts
    });
    if (missing) {
      this.error.set(missing);
      return;
    }
    if (this.validTo < this.validFrom) {
      this.error.set('Valid to must be on or after valid from.');
      return;
    }

    const payload: ContractPayload = {
      hotelName: this.hotelName.trim(),
      validFrom: this.validFrom,
      validTo: this.validTo,
      markup: Number(this.markup),
      roomTypes: this.roomTypes.map((room) => ({
        name: room.name.trim(),
        pricePerPersonPerNight: Number(room.pricePerPersonPerNight),
        numberOfRooms: Number(room.numberOfRooms),
        maxAdults: Number(room.maxAdults)
      }))
    };

    this.saving.set(true);
    const req$ =
      this.editingId == null
        ? this.contractService.create(payload)
        : this.contractService.update(this.editingId, payload);

    req$.subscribe({
      next: () => {
        this.saving.set(false);
        this.success.set(this.editingId ? 'Contract updated.' : 'Contract created.');
        this.clearForm();
        this.load();
      },
      error: (err) => {
        this.saving.set(false);
        this.error.set(err?.error?.message || 'Could not save contract.');
      }
    });
  }

  async remove(contract: ContractResponse): Promise<void> {
    const ok = await this.confirmDialog.ask({
      title: 'Delete contract',
      message: `Delete contract for ${contract.hotelName}?`,
      confirmText: 'Delete',
      cancelText: 'Keep',
      danger: true
    });
    if (!ok) {
      return;
    }
    this.contractService.delete(contract.id).subscribe({
      next: () => this.load(),
      error: (err) => this.error.set(err?.error?.message || 'Delete failed.')
    });
  }

  private blankRoom(): RoomTypePayload {
    return { name: '', pricePerPersonPerNight: 0, numberOfRooms: 1, maxAdults: 1 };
  }
}
