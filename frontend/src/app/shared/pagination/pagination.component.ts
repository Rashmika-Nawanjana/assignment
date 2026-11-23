import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-pagination',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="pager" *ngIf="totalPages > 1">
      <button type="button" [disabled]="page <= 0" (click)="change(page - 1)">Prev</button>
      <span>Page {{ page + 1 }} / {{ totalPages }} · {{ totalElements }} items</span>
      <button type="button" [disabled]="page >= totalPages - 1" (click)="change(page + 1)">
        Next
      </button>
    </div>
  `,
  styles: [
    `
      .pager {
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 1rem;
        margin-top: 1rem;
        font-size: 0.9rem;
        color: #5b6b76;
      }
      button {
        border: 1px solid #c9d5dc;
        background: #fff;
        padding: 0.4rem 0.8rem;
        cursor: pointer;
      }
      button:disabled {
        opacity: 0.45;
        cursor: not-allowed;
      }
    `
  ]
})
export class PaginationComponent {
  @Input() page = 0;
  @Input() totalPages = 0;
  @Input() totalElements = 0;
  @Output() pageChange = new EventEmitter<number>();

  change(next: number): void {
    this.pageChange.emit(next);
  }
}
