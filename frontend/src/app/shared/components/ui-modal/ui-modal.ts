import { Component, input, output, computed, effect } from '@angular/core';

export type ModalSize = 'sm' | 'md' | 'lg' | 'xl' | 'full';

@Component({
  selector: 'app-ui-modal',
  standalone: true,
  templateUrl: './ui-modal.html',
  styleUrl: './ui-modal.css',
})
export class UiModal {
  // Inputs
  isOpen = input<boolean>(false);
  size = input<ModalSize>('md');
  title = input<string>('');
  closeOnBackdrop = input<boolean>(true);
  closeOnEscape = input<boolean>(true);
  showClose = input<boolean>(true);

  // Outputs
  closed = output<void>();

  // Computed classes
  protected readonly modalClasses = computed(() => {
    const classes = ['modal-content'];
    classes.push(`modal-${this.size()}`);
    return classes.join(' ');
  });

  constructor() {
    // Handle escape key
    effect(() => {
      if (this.isOpen() && this.closeOnEscape()) {
        const handleEscape = (e: KeyboardEvent) => {
          if (e.key === 'Escape') {
            this.handleClose();
          }
        };
        document.addEventListener('keydown', handleEscape);
        return () => document.removeEventListener('keydown', handleEscape);
      }
      return;
    });

    // Prevent body scroll when modal is open
    effect(() => {
      if (this.isOpen()) {
        document.body.style.overflow = 'hidden';
      } else {
        document.body.style.overflow = '';
      }
    });
  }

  handleClose(): void {
    this.closed.emit();
  }

  handleBackdropClick(): void {
    if (this.closeOnBackdrop()) {
      this.handleClose();
    }
  }

  handleContentClick(event: MouseEvent): void {
    event.stopPropagation();
  }
}
