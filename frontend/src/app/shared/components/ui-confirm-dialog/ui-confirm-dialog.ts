import { Component, input, output, ChangeDetectionStrategy } from '@angular/core';
import { UiModal } from '../ui-modal/ui-modal';
import { UiButton } from '../ui-button/ui-button';
import { IconComponent } from '../icon/icon';

export interface ConfirmDialogData {
  title: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  variant?: 'primary' | 'secondary' | 'outline' | 'ghost' | 'danger';
  icon?: string;
}

@Component({
  selector: 'app-ui-confirm-dialog',
  standalone: true,
  imports: [UiModal, UiButton, IconComponent],
  templateUrl: './ui-confirm-dialog.html',
  styleUrl: './ui-confirm-dialog.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UiConfirmDialog {
  isOpen = input<boolean>(false);
  data = input<ConfirmDialogData>({
    title: 'Confirm Action',
    message: 'Are you sure you want to proceed?',
    confirmText: 'Confirm',
    cancelText: 'Cancel',
    variant: 'danger',
    icon: 'exclamation-triangle'
  });

  confirmed = output<void>();
  cancelled = output<void>();

  onConfirm(): void {
    this.confirmed.emit();
  }

  onCancel(): void {
    this.cancelled.emit();
  }
}

