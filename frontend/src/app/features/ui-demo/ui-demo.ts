import { Component, signal, ChangeDetectionStrategy } from '@angular/core';

// Import all shared components from barrel
import {
  ThemeToggleComponent,
  UiButton,
  UiInput,
  UiCard,
  UiModal,
  UiAlert,
  UiBadge,
  UiDropdown,
  Spinner,
  Skeleton,
  TooltipDirective,
} from '../../shared';

@Component({
  selector: 'app-ui-demo',
  standalone: true,
  imports: [
    ThemeToggleComponent,
    UiButton,
    UiInput,
    UiCard,
    UiModal,
    UiAlert,
    UiBadge,
    UiDropdown,
    Spinner,
    Skeleton,
    TooltipDirective,
  ],
  templateUrl: './ui-demo.html',
  styleUrl: './ui-demo.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UiDemoComponent {
  // Form state
  protected readonly email = signal('');
  protected readonly password = signal('');
  protected readonly emailError = signal('');

  // Modal state
  protected readonly isModalOpen = signal(false);

  // Alert state
  protected readonly showSuccessAlert = signal(true);
  protected readonly showWarningAlert = signal(true);
  protected readonly showErrorAlert = signal(true);
  protected readonly showInfoAlert = signal(true);

  // Loading states
  protected readonly isLoading = signal(false);

  // Demo methods
  openModal(): void {
    this.isModalOpen.set(true);
  }

  closeModal(): void {
    this.isModalOpen.set(false);
  }

  handleButtonClick(): void {
    console.log('Button clicked!');
  }

  handleSubmit(): void {
    if (!this.email()) {
      this.emailError.set('Email is required');
      return;
    }
    if (!this.email().includes('@')) {
      this.emailError.set('Please enter a valid email');
      return;
    }
    this.emailError.set('');
    console.log('Form submitted:', { email: this.email(), password: this.password() });
  }

  toggleLoading(): void {
    this.isLoading.set(true);
    setTimeout(() => this.isLoading.set(false), 2000);
  }

  dismissAlert(type: string): void {
    switch (type) {
      case 'success':
        this.showSuccessAlert.set(false);
        break;
      case 'warning':
        this.showWarningAlert.set(false);
        break;
      case 'error':
        this.showErrorAlert.set(false);
        break;
      case 'info':
        this.showInfoAlert.set(false);
        break;
    }
  }
}


