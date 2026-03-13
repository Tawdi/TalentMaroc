import { Component, inject, signal, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { Store } from '@ngrx/store';
import { DatePipe } from '@angular/common';
import {
  UiCard, UiButton, UiAlert, UiBadge, UiModal, UiTextarea,
  UiSelect, IconComponent, Skeleton,
} from '../../../../shared';
import {
  CompanySummaryResponse,
  ValidateCompanyRequest,
  CompanyStatus,
  COMPANY_STATUS_LABELS,
  getCompanyStatusVariant,
} from '../../../../core/models/company-offers.model';
import {
  CompanyActions,
  selectPendingCompanies,
  selectPendingLoading,
  selectPendingError,
  selectCompanySaving,
} from '../../store';

@Component({
  selector: 'app-admin-pending-companies',
  standalone: true,
  imports: [
    UiCard, UiButton, UiAlert, UiBadge, UiModal, UiTextarea,
    UiSelect, IconComponent, Skeleton, DatePipe,
  ],
  templateUrl: './admin-pending-companies.html',
  styleUrl: './admin-pending-companies.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminPendingCompaniesComponent implements OnInit {
  private readonly store = inject(Store);

  readonly companies = this.store.selectSignal(selectPendingCompanies);
  readonly loading = this.store.selectSignal(selectPendingLoading);
  readonly error = this.store.selectSignal(selectPendingError);
  readonly saving = this.store.selectSignal(selectCompanySaving);

  readonly statusLabels = COMPANY_STATUS_LABELS;
  readonly getStatusVariant = getCompanyStatusVariant;

  // Validate modal
  isValidateModalOpen = signal(false);
  selectedCompany = signal<CompanySummaryResponse | null>(null);
  validateDecision = signal('');
  validateReason = signal('');

  readonly decisionOptions = [
    { value: 'APPROVED', label: '✓ Approve' },
    { value: 'REJECTED', label: '✕ Reject' },
  ];

  ngOnInit(): void {
    this.store.dispatch(CompanyActions.loadPendingCompanies());
  }

  openValidate(company: CompanySummaryResponse): void {
    this.selectedCompany.set(company);
    this.validateDecision.set('');
    this.validateReason.set('');
    this.isValidateModalOpen.set(true);
  }

  closeValidate(): void {
    this.isValidateModalOpen.set(false);
    this.selectedCompany.set(null);
  }

  submitValidation(): void {
    const company = this.selectedCompany();
    const decision = this.validateDecision();
    if (!company || !decision) return;

    const request: ValidateCompanyRequest = {
      status: decision as CompanyStatus,
      reason: this.validateReason().trim() || undefined,
    };

    this.store.dispatch(CompanyActions.validateCompany({ companyId: company.id, request }));
    this.isValidateModalOpen.set(false);
    this.selectedCompany.set(null);
  }

  refresh(): void {
    this.store.dispatch(CompanyActions.loadPendingCompanies());
  }
}



