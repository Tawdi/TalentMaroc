import { Component, inject, signal, computed, ChangeDetectionStrategy } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { UiCard, UiInput, UiTextarea, UiButton, UiAlert, IconComponent } from '../../../../shared';

@Component({
  selector: 'app-public-company-register',
  standalone: true,
  imports: [UiCard, UiInput, UiTextarea, UiButton, UiAlert, IconComponent, RouterLink],
  templateUrl: './public-company-register.html',
  styleUrl: './public-company-register.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PublicCompanyRegisterComponent {
  private readonly router = inject(Router);

  // Loading/error state
  saving = signal(false);
  error = signal<string | null>(null);
  success = signal(false);

  // Account fields
  email = signal('');
  password = signal('');
  confirmPassword = signal('');

  // Company fields
  companyName = signal('');
  sector = signal('');
  description = signal('');
  website = signal('');
  phone = signal('');
  street = signal('');
  city = signal('');
  state = signal('');
  zipCode = signal('');
  country = signal('');

  // Validation errors
  emailError = signal('');
  passwordError = signal('');
  confirmPasswordError = signal('');
  companyNameError = signal('');
  sectorError = signal('');

  validate(): boolean {
    let valid = true;

    // Clear all errors
    this.emailError.set('');
    this.passwordError.set('');
    this.confirmPasswordError.set('');
    this.companyNameError.set('');
    this.sectorError.set('');

    // Validate email
    if (!this.email().trim()) {
      this.emailError.set('Email is required');
      valid = false;
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.email())) {
      this.emailError.set('Please enter a valid email address');
      valid = false;
    }

    // Validate password
    if (!this.password()) {
      this.passwordError.set('Password is required');
      valid = false;
    } else if (this.password().length < 6) {
      this.passwordError.set('Password must be at least 6 characters');
      valid = false;
    }

    // Validate confirm password
    if (!this.confirmPassword()) {
      this.confirmPasswordError.set('Please confirm your password');
      valid = false;
    } else if (this.password() !== this.confirmPassword()) {
      this.confirmPasswordError.set('Passwords do not match');
      valid = false;
    }

    // Validate company name
    if (!this.companyName().trim()) {
      this.companyNameError.set('Company name is required');
      valid = false;
    }

    // Validate sector
    if (!this.sector().trim()) {
      this.sectorError.set('Business sector is required');
      valid = false;
    }

    return valid;
  }

  async submit(): Promise<void> {
    if (!this.validate()) return;

    this.saving.set(true);
    this.error.set(null);

    try {
      // TODO: Call backend API to register company
      // This would create both user account and company profile
      const registrationData = {
        // User account data
        email: this.email().trim(),
        password: this.password(),
        role: 'COMPANY',

        // Company profile data
        companyName: this.companyName().trim(),
        sector: this.sector().trim(),
        description: this.description().trim() || undefined,
        website: this.website().trim() || undefined,
        phone: this.phone().trim() || undefined,
        address: {
          street: this.street().trim() || undefined,
          city: this.city().trim() || undefined,
          state: this.state().trim() || undefined,
          zipCode: this.zipCode().trim() || undefined,
          country: this.country().trim() || undefined,
        },
      };

      console.log('Company registration data:', registrationData);

      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1500));

      this.success.set(true);
    } catch (err) {
      this.error.set('Registration failed. Please try again.');
    } finally {
      this.saving.set(false);
    }
  }

  goToLogin(): void {
    this.router.navigate(['/auth/login']);
  }

  clearError(): void {
    this.error.set(null);
  }
}


