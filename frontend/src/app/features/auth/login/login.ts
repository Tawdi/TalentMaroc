import { Component, signal, inject, ChangeDetectionStrategy } from '@angular/core';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { UiButton } from '../../../shared';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterLink, UiButton],
  templateUrl: './login.html',
  styleUrl: './login.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  // Form state
  protected readonly email = signal('');
  protected readonly password = signal('');
  protected readonly rememberMe = signal(false);

  // Error states
  protected readonly emailError = signal('');
  protected readonly passwordError = signal('');
  protected readonly generalError = signal('');

  // Loading state from auth service
  protected readonly isLoading = this.authService.isLoading;

  // Password visibility
  protected readonly showPassword = signal(false);

  togglePasswordVisibility(): void {
    this.showPassword.update(v => !v);
  }

  toggleRememberMe(): void {
    this.rememberMe.update(v => !v);
  }

  validateForm(): boolean {
    let isValid = true;

    // Reset errors
    this.emailError.set('');
    this.passwordError.set('');
    this.generalError.set('');

    // Validate email
    if (!this.email()) {
      this.emailError.set('Email is required');
      isValid = false;
    } else if (!this.isValidEmail(this.email())) {
      this.emailError.set('Please enter a valid email address');
      isValid = false;
    }

    // Validate password
    if (!this.password()) {
      this.passwordError.set('Password is required');
      isValid = false;
    }

    return isValid;
  }

  isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  handleSubmit(event: Event): void {
    event.preventDefault();

    if (!this.validateForm()) {
      return;
    }

    this.authService.login({
      email: this.email(),
      password: this.password(),
    }).subscribe({
      next: (response) => {
        if (response.status === 'SUCCESS') {
          // Get return URL from query params or default to dashboard
          const returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/dashboard';
          this.router.navigateByUrl(returnUrl);
        }
      },
      error: (error) => {
        this.generalError.set(error.message || 'Login failed. Please try again.');
      },
    });
  }

  startOAuthLogin(provider: 'google' | 'github'): void {
    const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl') ?? '/dashboard';
    this.authService.startOAuthLogin(provider, returnUrl);
  }
}
