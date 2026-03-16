import { Component, inject, signal, computed, ChangeDetectionStrategy } from '@angular/core';
import { Store } from '@ngrx/store';
import { RouterLink } from '@angular/router';
import { UiCard, UiInput, UiTextarea, UiButton, UiAlert, IconComponent } from '../../../../shared';
import { AuthService } from '../../../../core/services/auth.service';
import { CreateCompanyRequest } from '../../../../core/models/company-offers.model';
import {
  CompanyActions,
  selectCompanySaving,
  selectCompanyError,
  selectCompany,
} from '../../store';

@Component({
  selector: 'app-company-register',
  standalone: true,
  imports: [UiCard, UiInput, UiTextarea, UiButton, UiAlert, IconComponent, RouterLink],
  templateUrl: './company-register.html',
  styleUrl: './company-register.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CompanyRegisterComponent {
  private readonly store = inject(Store);
  private readonly authService = inject(AuthService);

  readonly userId = computed(() => this.authService.currentUser()?.id ?? '');
  readonly saving = this.store.selectSignal(selectCompanySaving);
  readonly error = this.store.selectSignal(selectCompanyError);
  readonly company = this.store.selectSignal(selectCompany);

  // Form fields
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

  // Validation
  companyNameError = signal('');
  sectorError = signal('');

  readonly success = computed(() => !!this.company() && this.company()!.status === 'PENDING');

  validate(): boolean {
    let valid = true;
    this.companyNameError.set('');
    this.sectorError.set('');

    if (!this.companyName().trim()) {
      this.companyNameError.set('Company name is required');
      valid = false;
    }
    if (!this.sector().trim()) {
      this.sectorError.set('Sector is required');
      valid = false;
    }
    return valid;
  }

  handleSubmit(event: Event): void {
    event.preventDefault();
    if (!this.validate()) return;

    const request: CreateCompanyRequest = {
      userId: this.userId(),
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

    this.store.dispatch(CompanyActions.createCompany({ request }));
  }

  clearError(): void {
    this.store.dispatch(CompanyActions.clearError());
  }
}

