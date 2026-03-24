import { Component, ChangeDetectionStrategy, inject, signal } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { UiButton } from '../../../shared';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-verify-email',
  standalone: true,
  imports: [UiButton],
  templateUrl: './verify-email.html',
  styleUrl: './verify-email.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerifyEmailComponent {
  private readonly authService = inject(AuthService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly token = signal('');
  protected readonly statusMessage = signal('');
  protected readonly errorMessage = signal('');
  protected readonly isVerifying = signal(false);

  constructor() {
    const urlToken = this.route.snapshot.queryParamMap.get('token');
    if (urlToken) {
      this.token.set(urlToken);
      this.verify();
    }
  }

  handleSubmit(event: Event): void {
    event.preventDefault();
    this.verify();
  }

  private verify(): void {
    const value = this.token().trim();
    if (!value) {
      this.errorMessage.set('Verification token is required');
      this.statusMessage.set('');
      return;
    }

    this.isVerifying.set(true);
    this.statusMessage.set('');
    this.errorMessage.set('');

    this.authService.verifyEmail(value).subscribe({
      next: (res) => {
        this.isVerifying.set(false);
        this.statusMessage.set(res.message || 'Email verified successfully. You can now login.');
      },
      error: (err) => {
        this.isVerifying.set(false);
        this.errorMessage.set(err.message || 'Email verification failed.');
      },
    });
  }

  navigateToLogin(): void {
    this.router.navigate(['/auth/login']);
  }

  navigateToResend(): void {
    this.router.navigate(['/auth/resend-verification']);
  }
}
