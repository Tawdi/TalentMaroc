import { Component, inject, computed, signal, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
  UiCard, UiInput, UiTextarea, UiButton, UiAlert, UiBadge,
  UiModal, IconComponent, Skeleton, UiConfirmDialog, ConfirmDialogData,
} from '../../../../shared';
import { AuthService } from '../../../../core/services/auth.service';
import {
  CompanyResponse,
  UpdateCompanyRequest,
  COMPANY_STATUS_LABELS,
  getCompanyStatusVariant,
} from '../../../../core/models/company-offers.model';
import {
  CompanyActions,
  selectCompany,
  selectCompanyLoading,
  selectCompanyError,
  selectCompanyNotFound,
  selectCompanySaving,
} from '../../store';

@Component({
  selector: 'app-company-profile',
  standalone: true,
  imports: [
    UiCard, UiInput, UiTextarea, UiButton, UiAlert, UiBadge,
    UiModal, IconComponent, Skeleton, UiConfirmDialog,
  ],
  templateUrl: './company-profile.html',
  styleUrl: './company-profile.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CompanyProfileComponent implements OnInit {
  private readonly store = inject(Store);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly userId = computed(() => this.authService.currentUser()?.id ?? '');
  readonly company = this.store.selectSignal(selectCompany);
  readonly loading = this.store.selectSignal(selectCompanyLoading);
  readonly error = this.store.selectSignal(selectCompanyError);
  readonly notFound = this.store.selectSignal(selectCompanyNotFound);
  readonly saving = this.store.selectSignal(selectCompanySaving);

  readonly statusLabels = COMPANY_STATUS_LABELS;
  readonly getStatusVariant = getCompanyStatusVariant;

  // Edit modal state
  isEditOpen = signal(false);
  editName = signal('');
  editSector = signal('');
  editDescription = signal('');
  editWebsite = signal('');
  editPhone = signal('');
  editStreet = signal('');
  editCity = signal('');
  editState = signal('');
  editZipCode = signal('');
  editCountry = signal('');

  // Delete confirm
  showDeleteConfirm = signal(false);
  deleteConfirmData: ConfirmDialogData = {
    title: 'Delete Company',
    message: 'Are you sure you want to delete your company? This action cannot be undone and all offers will be removed.',
    confirmText: 'Delete',
    cancelText: 'Cancel',
    variant: 'danger',
    icon: 'exclamation-triangle',
  };

  ngOnInit(): void {
    const uid = this.userId();
    if (uid) {
      this.store.dispatch(CompanyActions.loadCompany({ userId: uid }));
    }
  }

  goToRegister(): void {
    this.router.navigate(['/dashboard/company-register']);
  }

  goToOffers(): void {
    this.router.navigate(['/dashboard/offers']);
  }

  openEdit(): void {
    const c = this.company();
    if (!c) return;
    this.editName.set(c.companyName);
    this.editSector.set(c.sector);
    this.editDescription.set(c.description ?? '');
    this.editWebsite.set(c.website ?? '');
    this.editPhone.set(c.phone ?? '');
    this.editStreet.set(c.address?.street ?? '');
    this.editCity.set(c.address?.city ?? '');
    this.editState.set(c.address?.state ?? '');
    this.editZipCode.set(c.address?.zipCode ?? '');
    this.editCountry.set(c.address?.country ?? '');
    this.isEditOpen.set(true);
  }

  closeEdit(): void {
    this.isEditOpen.set(false);
  }

  saveEdit(): void {
    const request: UpdateCompanyRequest = {
      companyName: this.editName().trim() || undefined,
      sector: this.editSector().trim() || undefined,
      description: this.editDescription().trim() || undefined,
      website: this.editWebsite().trim() || undefined,
      phone: this.editPhone().trim() || undefined,
      address: {
        street: this.editStreet().trim() || undefined,
        city: this.editCity().trim() || undefined,
        state: this.editState().trim() || undefined,
        zipCode: this.editZipCode().trim() || undefined,
        country: this.editCountry().trim() || undefined,
      },
    };
    this.store.dispatch(CompanyActions.updateCompany({ userId: this.userId(), request }));
    this.isEditOpen.set(false);
  }

  confirmDeleteCompany(): void {
    this.showDeleteConfirm.set(true);
  }

  deleteCompany(): void {
    this.store.dispatch(CompanyActions.deleteCompany({ userId: this.userId() }));
    this.showDeleteConfirm.set(false);
  }

  cancelDelete(): void {
    this.showDeleteConfirm.set(false);
  }

  clearError(): void {
    this.store.dispatch(CompanyActions.clearError());
  }
}

