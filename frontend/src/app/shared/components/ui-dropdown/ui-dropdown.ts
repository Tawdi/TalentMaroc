import { Component, input, output, signal, HostListener } from '@angular/core';

@Component({
  selector: 'app-ui-dropdown',
  standalone: true,
  templateUrl: './ui-dropdown.html',
  styleUrl: './ui-dropdown.css',
})
export class UiDropdown {
  // Inputs
  label = input<string>('');
  align = input<'left' | 'right'>('left');

  // Outputs
  opened = output<void>();
  closed = output<void>();

  // State
  protected readonly isOpen = signal(false);

  toggle(): void {
    const newState = !this.isOpen();
    this.isOpen.set(newState);

    if (newState) {
      this.opened.emit();
    } else {
      this.closed.emit();
    }
  }

  close(): void {
    if (this.isOpen()) {
      this.isOpen.set(false);
      this.closed.emit();
    }
  }

  @HostListener('document:click', ['$event'])
  handleClickOutside(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.dropdown')) {
      this.close();
    }
  }
}

