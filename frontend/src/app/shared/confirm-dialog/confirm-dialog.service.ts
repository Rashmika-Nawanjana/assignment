import { Injectable, signal } from '@angular/core';

export interface ConfirmOptions {
  title?: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  danger?: boolean;
}

interface ConfirmState extends ConfirmOptions {
  open: boolean;
  resolve: ((value: boolean) => void) | null;
}

@Injectable({ providedIn: 'root' })
export class ConfirmDialogService {
  readonly state = signal<ConfirmState>({
    open: false,
    message: '',
    resolve: null
  });

  ask(options: ConfirmOptions | string): Promise<boolean> {
    const normalized: ConfirmOptions =
      typeof options === 'string' ? { message: options } : options;

    return new Promise<boolean>((resolve) => {
      this.state.set({
        open: true,
        title: normalized.title || 'Please confirm',
        message: normalized.message,
        confirmText: normalized.confirmText || 'Confirm',
        cancelText: normalized.cancelText || 'Cancel',
        danger: normalized.danger ?? true,
        resolve
      });
    });
  }

  confirm(): void {
    const current = this.state();
    current.resolve?.(true);
    this.close();
  }

  cancel(): void {
    const current = this.state();
    current.resolve?.(false);
    this.close();
  }

  private close(): void {
    this.state.set({
      open: false,
      message: '',
      resolve: null
    });
  }
}
