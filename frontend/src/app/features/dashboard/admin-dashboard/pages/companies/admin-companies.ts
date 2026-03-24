import { Component, inject, OnInit, signal, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Store } from '@ngrx/store';
import { CompanyStatus, ValidateCompanyRequest } from '../../../../../core/models/company-offers.model';
import { UiCard, UiBadge, UiButton, IconComponent, UiConfirmDialog, ConfirmDialogData, UiAlert } from '../../../../../shared';
import { AdminActions } from '../../store/admin.actions';
import * as AdminSelectors from '../../store/admin.selectors';

type ConfirmAction = 'DELETE' | 'APPROVE' | 'REJECT';

@Component({
  selector: 'app-admin-companies',
  standalone: true,
  imports: [CommonModule, UiCard, UiBadge, UiButton, IconComponent, UiConfirmDialog, UiAlert],
  templateUrl: './admin-companies.html',
  styleUrl: './admin-companies.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AdminCompaniesComponent implements OnInit {
  private store = inject(Store);

  companies = this.store.selectSignal(AdminSelectors.selectCompanies);
  totalElements = this.store.selectSignal(AdminSelectors.selectCompaniesTotalElements);
  page = this.store.selectSignal(AdminSelectors.selectCompaniesPage);
  size = this.store.selectSignal(AdminSelectors.selectCompaniesSize);
  loading = this.store.selectSignal(AdminSelectors.selectCompaniesLoading);
  error = this.store.selectSignal(AdminSelectors.selectCompaniesError);

  // Confirm dialog
  showConfirmDialog = signal(false);
  confirmTarget = signal<number | null>(null);
  confirmAction = signal<ConfirmAction | null>(null);
  confirmDialogData = signal<ConfirmDialogData>({
    title: '',
    message: '',
    confirmText: 'Confirm',
    cancelText: 'Cancel',
    variant: 'danger',
    icon: 'exclamation-triangle',
  });

  ngOnInit() {
    this.loadCompanies(0);
  }

  loadCompanies(page: number) {
    this.store.dispatch(AdminActions.loadCompanies({ page }));
  }

  validateCompany(companyId: number, status: string) {
    const action = status === 'APPROVED' ? 'APPROVE' : 'REJECT';
    this.confirmTarget.set(companyId);
    this.confirmAction.set(action);
    this.confirmDialogData.set({
      title: `${status === 'APPROVED' ? 'Approve' : 'Reject'} Company`,
      message: `Are you sure you want to ${status.toLowerCase()} this company?`,
      confirmText: status === 'APPROVED' ? 'Approve' : 'Reject',
      cancelText: 'Cancel',
      variant: status === 'APPROVED' ? 'primary' : 'danger',
      icon: status === 'APPROVED' ? 'check' : 'x-mark',
    });
    this.showConfirmDialog.set(true);
  }

  deleteCompany(companyId: number) {
    this.confirmTarget.set(companyId);
    this.confirmAction.set('DELETE');
    this.confirmDialogData.set({
      title: 'Delete Company',
      message: 'Are you sure you want to delete this company? This action cannot be undone.',
      confirmText: 'Delete',
      cancelText: 'Cancel',
      variant: 'danger',
      icon: 'trash',
    });
    this.showConfirmDialog.set(true);
  }

  confirmOperation() {
    const companyId = this.confirmTarget();
    const action = this.confirmAction();

    if (companyId && action) {
      if (action === 'DELETE') {
        this.store.dispatch(AdminActions.deleteCompany({ companyId }));
      } else {
        const status: CompanyStatus = action === 'APPROVE' ? 'APPROVED' : 'REJECTED';
        const request: ValidateCompanyRequest = { status };
        this.store.dispatch(AdminActions.validateCompany({ companyId, request }));
      }
      this.cancelConfirm();
    }
  }

  cancelConfirm() {
    this.showConfirmDialog.set(false);
    this.confirmTarget.set(null);
    this.confirmAction.set(null);
  }

  clearError() {
    this.store.dispatch(AdminActions.clearError());
  }

  nextPage() {
    if ((this.page() + 1) * this.size() < this.totalElements()) {
      this.loadCompanies(this.page() + 1);
    }
  }

  prevPage() {
    if (this.page() > 0) {
      this.loadCompanies(this.page() - 1);
    }
  }
}

