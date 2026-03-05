import { Component, signal, inject, ChangeDetectionStrategy } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { UiButton } from '../../../shared';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [RouterLink, UiButton],
  templateUrl: './register.html',
  styleUrl: './register.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegisterComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  // Form state
  protected readonly firstName = signal('');
  protected readonly lastName = signal('');
  protected readonly username = signal('');
  protected readonly email = signal('');
  protected readonly password = signal('');
  protected readonly confirmPassword = signal('');
  protected readonly acceptTerms = signal(false);

  // Error states
  protected readonly firstNameError = signal('');
  protected readonly lastNameError = signal('');
  protected readonly usernameError = signal('');
  protected readonly emailError = signal('');
  protected readonly passwordError = signal('');
  protected readonly confirmPasswordError = signal('');
  protected readonly termsError = signal('');
  protected readonly generalError = signal('');

  // Success state
  protected readonly registrationSuccess = signal(false);

  // Loading state from auth service
  protected readonly isLoading = this.authService.isLoading;

  // Password visibility
  protected readonly showPassword = signal(false);
  protected readonly showConfirmPassword = signal(false);

  // Password strength
  protected readonly passwordStrength = signal(0);
  protected readonly passwordStrengthLabel = signal('');

  togglePasswordVisibility(): void {
    this.showPassword.update(v => !v);
  }

  toggleConfirmPasswordVisibility(): void {
    this.showConfirmPassword.update(v => !v);
  }

  toggleTerms(): void {
    this.acceptTerms.update(v => !v);
    if (this.acceptTerms()) {
      this.termsError.set('');
    }
  }

  onPasswordChange(value: string): void {
    this.password.set(value);
    this.calculatePasswordStrength(value);
  }

  calculatePasswordStrength(password: string): void {
    let strength = 0;

    if (password.length >= 8) strength++;
    if (password.length >= 12) strength++;
    if (/[a-z]/.test(password) && /[A-Z]/.test(password)) strength++;
    if (/\d/.test(password)) strength++;
    if (/[^a-zA-Z0-9]/.test(password)) strength++;

    this.passwordStrength.set(strength);

    const labels = ['Very Weak', 'Weak', 'Fair', 'Good', 'Strong'];
    this.passwordStrengthLabel.set(password ? labels[Math.min(strength, 4)] : '');
  }

  // Generate username from name
  generateUsername(): void {
    const first = this.firstName().toLowerCase().trim();
    const last = this.lastName().toLowerCase().trim();
    if (first && last) {
      const random = Math.floor(Math.random() * 1000);
      this.username.set(`${first}.${last}${random}`);
    }
  }

  validateForm(): boolean {
    let isValid = true;

    // Reset errors
    this.firstNameError.set('');
    this.lastNameError.set('');
    this.usernameError.set('');
    this.emailError.set('');
    this.passwordError.set('');
    this.confirmPasswordError.set('');
    this.termsError.set('');
    this.generalError.set('');

    // Validate first name
    if (!this.firstName().trim()) {
      this.firstNameError.set('First name is required');
      isValid = false;
    } else if (this.firstName().length < 2) {
      this.firstNameError.set('First name must be at least 2 characters');
      isValid = false;
    }

    // Validate last name
    if (!this.lastName().trim()) {
      this.lastNameError.set('Last name is required');
      isValid = false;
    } else if (this.lastName().length < 2) {
      this.lastNameError.set('Last name must be at least 2 characters');
      isValid = false;
    }

    // Validate username
    if (!this.username().trim()) {
      // Auto-generate username if not provided
      this.generateUsername();
    }
    if (this.username().length < 3) {
      this.usernameError.set('Username must be at least 3 characters');
      isValid = false;
    } else if (this.username().length > 20) {
      this.usernameError.set('Username must be at most 20 characters');
      isValid = false;
    }

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
    } else if (this.password().length < 6) {
      this.passwordError.set('Password must be at least 6 characters');
      isValid = false;
    } else if (this.passwordStrength() < 2) {
      this.passwordError.set('Please choose a stronger password');
      isValid = false;
    }

    // Validate confirm password
    if (!this.confirmPassword()) {
      this.confirmPasswordError.set('Please confirm your password');
      isValid = false;
    } else if (this.password() !== this.confirmPassword()) {
      this.confirmPasswordError.set('Passwords do not match');
      isValid = false;
    }

    // Validate terms
    if (!this.acceptTerms()) {
      this.termsError.set('You must accept the terms and conditions');
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

    // Auto-generate username if empty
    if (!this.username().trim()) {
      this.generateUsername();
    }

    if (!this.validateForm()) {
      return;
    }

    this.authService.register({
      email: this.email(),
      username: this.username(),
      password: this.password(),
      name: `${this.firstName()} ${this.lastName()}`,
      roleName: 'CANDIDATE', // Always register as candidate
    }).subscribe({
      next: (response) => {
        if (response.status === 'SUCCESS') {
          this.registrationSuccess.set(true);
        }
      },
      error: (error) => {
        this.generalError.set(error.message || 'Registration failed. Please try again.');
      },
    });
  }
}

