import { Component, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { UiCard, UiInput, UiTextarea, UiButton, UiAlert, IconComponent } from '../../../../shared';
import { AuthService, CompanyService, CreateCompanyRequest, RegisterRequest } from '../../../../core';
import { catchError, finalize, map, of, switchMap, throwError } from 'rxjs';

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

  private readonly authService = inject(AuthService);
  private readonly companyService = inject(CompanyService);

  // Loading/error state
  saving = signal(false);
  error = signal<string | null>(null);
  success = signal(false);

  // Account fields
  email = signal('');
  password = signal('');
  username = signal('');
  userId = signal<string|null>('');
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
  usernameError = signal('');

  // Stepper
  currentStep = signal(1);
  readonly steps = [
    { id: 1, label: 'Account Details' },
    { id: 2, label: 'Company Details' },
    { id: 3, label: 'Address' },
  ];

  validateStep(step: number): boolean {
    let valid = true;

    if (step === 1) {
      this.emailError.set('');
      this.passwordError.set('');
      this.confirmPasswordError.set('');
      this.usernameError.set('');

      if (!this.email().trim()) {
        this.emailError.set('Email is required');
        valid = false;
      } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.email())) {
        this.emailError.set('Please enter a valid email address');
        valid = false;
      }

      if (!this.username().trim()) {
        this.usernameError.set('Username is required');
        valid = false;
      }

      if (!this.password()) {
        this.passwordError.set('Password is required');
        valid = false;
      } else if (this.password().length < 6) {
        this.passwordError.set('Password must be at least 6 characters');
        valid = false;
      }

      if (!this.confirmPassword()) {
        this.confirmPasswordError.set('Please confirm your password');
        valid = false;
      } else if (this.password() !== this.confirmPassword()) {
        this.confirmPasswordError.set('Passwords do not match');
        valid = false;
      }
    }

    if (step === 2) {
      this.companyNameError.set('');
      this.sectorError.set('');

      if (!this.companyName().trim()) {
        this.companyNameError.set('Company name is required');
        valid = false;
      }

      if (!this.sector().trim()) {
        this.sectorError.set('Business sector is required');
        valid = false;
      }
    }

    return valid;
  }

  nextStep(): void {
    const step = this.currentStep();
    if (!this.validateStep(step)) return;
    if (step < 3) {
      this.currentStep.set(step + 1);
    }
  }

  prevStep(): void {
    const step = this.currentStep();
    if (step > 1) {
      this.currentStep.set(step - 1);
    }
  }

  handleSubmit(event: Event): void {
    event.preventDefault();

    if (this.currentStep() < 3) {
      this.nextStep();
      return;
    }

    if (!this.validateStep(1) || !this.validateStep(2)) return;

    this.saving.set(true);
    this.error.set(null);

    const registerRequest: RegisterRequest = {
      email: this.email().trim(),
      password: this.password(),
      username: this.username().trim(),
      name: this.companyName().trim(),
    };

    this.authService.registerCompany(registerRequest).pipe(
      map((response) => response.data?.id ?? ''),
      switchMap((userId) => {
        if (!userId) {
          return throwError(() => new Error('User registration failed.'));
        }
        this.userId.set(userId);
        const requestCompany: CreateCompanyRequest = {
          userId,
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
        return this.companyService.createCompany(requestCompany);
      }),
      map(() => true),
      catchError((err) => {
        this.error.set(err?.error?.message || err?.message || 'Registration failed. Please try again.');
        return of(false);
      }),
      finalize(() => this.saving.set(false))
    ).subscribe((success) => {
      if (success) {
        this.success.set(true);
      }
    });
  }

  goToLogin(): void {
    this.router.navigate(['/auth/login']);
  }

  clearError(): void {
    this.error.set(null);
  }

  generateUsername(): void {
    const first = this.companyName().trim().split(' ')[0];
    if (first) {
      const random = Math.floor(Math.random() * 1000);
      this.username.set(`${first}.${random}`);
    }
  }
}
