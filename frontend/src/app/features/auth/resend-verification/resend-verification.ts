import { Component, ChangeDetectionStrategy, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { UiButton } from '../../../shared';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-resend-verification',
  standalone: true,
  imports: [RouterLink, UiButton],
  templateUrl: './resend-verification.html',
  styleUrl: './resend-verification.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ResendVerificationComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly email = signal('');
  protected readonly emailError = signal('');
  protected readonly statusMessage = signal('');
  protected readonly errorMessage = signal('');
  protected readonly isSubmitting = signal(false);

  private isValidEmail(value: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(value);
  }

  private validate(): boolean {
    this.emailError.set('');
    const value = this.email().trim();

    if (!value) {
      this.emailError.set('Email is required');
      return false;
    }
    if (!this.isValidEmail(value)) {
      this.emailError.set('Please enter a valid email address');
      return false;
    }
    return true;
  }

  handleSubmit(event: Event): void {
    event.preventDefault();

    this.statusMessage.set('');
    this.errorMessage.set('');

    if (!this.validate()) {
      return;
    }

    this.isSubmitting.set(true);

    this.authService.resendVerificationEmail(this.email()).subscribe({
      next: (res) => {
        this.isSubmitting.set(false);
        this.statusMessage.set(res.message || 'Verification email sent. Please check your inbox.');
      },
      error: (err) => {
        this.isSubmitting.set(false);
        this.errorMessage.set(err.message || 'Failed to send verification email.');
      },
    });
  }

  navigateToLogin(): void {
    this.router.navigate(['/auth/login']);
  }
}

