import { Component, ChangeDetectionStrategy, inject, signal } from '@angular/core';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { UiButton } from '../../../shared';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [RouterLink, UiButton],
  templateUrl: './reset-password.html',
  styleUrl: './reset-password.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ResetPasswordComponent {
  private readonly authService = inject(AuthService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly token = signal('');
  protected readonly password = signal('');
  protected readonly confirmPassword = signal('');

  protected readonly tokenError = signal('');
  protected readonly passwordError = signal('');
  protected readonly confirmPasswordError = signal('');
  protected readonly generalError = signal('');

  protected readonly statusMessage = signal('');
  protected readonly isSubmitting = signal(false);

  constructor() {
    const urlToken = this.route.snapshot.queryParamMap.get('token');
    if (urlToken) {
      this.token.set(urlToken);
    }
  }

  private validate(): boolean {
    let isValid = true;
    this.tokenError.set('');
    this.passwordError.set('');
    this.confirmPasswordError.set('');
    this.generalError.set('');

    const tokenVal = this.token().trim();
    const passVal = this.password();
    const confirmVal = this.confirmPassword();

    if (!tokenVal) {
      this.tokenError.set('Reset token is required');
      isValid = false;
    }

    if (!passVal) {
      this.passwordError.set('New password is required');
      isValid = false;
    } else if (passVal.length < 6) {
      this.passwordError.set('Password must be at least 6 characters');
      isValid = false;
    }

    if (!confirmVal) {
      this.confirmPasswordError.set('Please confirm your password');
      isValid = false;
    } else if (passVal !== confirmVal) {
      this.confirmPasswordError.set('Passwords do not match');
      isValid = false;
    }

    return isValid;
  }

  handleSubmit(event: Event): void {
    event.preventDefault();

    this.statusMessage.set('');
    this.generalError.set('');

    if (!this.validate()) {
      return;
    }

    this.isSubmitting.set(true);

    this.authService.resetPassword(this.token().trim(), this.password()).subscribe({
      next: (res) => {
        this.isSubmitting.set(false);
        this.statusMessage.set(res.message || 'Password reset successful. You can now login.');
      },
      error: (err) => {
        this.isSubmitting.set(false);
        this.generalError.set(err.message || 'Password reset failed.');
      },
    });
  }

  navigateToLogin(): void {
    this.router.navigate(['/auth/login']);
  }

  navigateToForgot(): void {
    this.router.navigate(['/auth/forgot-password']);
  }
}

