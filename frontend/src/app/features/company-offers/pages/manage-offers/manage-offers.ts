import { Component, inject, computed, signal, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { Store } from '@ngrx/store';
import { DatePipe, SlicePipe } from '@angular/common';
import {
  UiCard, UiInput, UiTextarea, UiButton, UiAlert, UiBadge,
  UiModal, UiSelect, UiDateInput, IconComponent, Skeleton,
  UiConfirmDialog, ConfirmDialogData,
} from '../../../../shared';
import { AuthService } from '../../../../core/services/auth.service';
import {
  OfferResponse,
  CreateOfferRequest,
  UpdateOfferRequest,
  OfferContractType,
  OfferStatus,
  OFFER_STATUS_LABELS,
  OFFER_CONTRACT_TYPE_LABELS,
  OFFER_CONTRACT_TYPE_OPTIONS,
  getOfferStatusVariant,
} from '../../../../core/models/company-offers.model';
import {
  OfferActions,
  selectMyOffers,
  selectMyOffersLoading,
  selectMyOffersTotalElements,
  selectMyOffersTotalPages,
  selectMyOffersCurrentPage,
  selectOfferSaving,
  selectOfferError,
} from '../../store';

@Component({
  selector: 'app-manage-offers',
  standalone: true,
  imports: [
    UiCard, UiInput, UiTextarea, UiButton, UiAlert, UiBadge,
    UiModal, UiSelect, UiDateInput, IconComponent, Skeleton,
    UiConfirmDialog, DatePipe, SlicePipe,
  ],
  templateUrl: './manage-offers.html',
  styleUrl: './manage-offers.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManageOffersComponent implements OnInit {
  private readonly store = inject(Store);
  private readonly authService = inject(AuthService);

  readonly userId = computed(() => this.authService.currentUser()?.id ?? '');
  readonly offers = this.store.selectSignal(selectMyOffers);
  readonly loading = this.store.selectSignal(selectMyOffersLoading);
  readonly totalElements = this.store.selectSignal(selectMyOffersTotalElements);
  readonly totalPages = this.store.selectSignal(selectMyOffersTotalPages);
  readonly currentPage = this.store.selectSignal(selectMyOffersCurrentPage);
  readonly saving = this.store.selectSignal(selectOfferSaving);
  readonly error = this.store.selectSignal(selectOfferError);

  readonly statusLabels = OFFER_STATUS_LABELS;
  readonly contractLabels = OFFER_CONTRACT_TYPE_LABELS;
  readonly getStatusVariant = getOfferStatusVariant;
  readonly contractOptions = OFFER_CONTRACT_TYPE_OPTIONS.map(o => ({ value: o.value, label: o.label }));

  // Modal state
  isModalOpen = signal(false);
  editingOffer = signal<OfferResponse | null>(null);

  // Form fields
  formTitle = signal('');
  formDescription = signal('');
  formContractType = signal('');
  formLocation = signal('');
  formSalaryRange = signal('');
  formRequirements = signal('');
  formBenefits = signal('');
  formExpiresAt = signal('');

  // Validation
  titleError = signal('');
  descriptionError = signal('');
  contractTypeError = signal('');

  // Confirm dialog
  showConfirmDialog = signal(false);
  confirmAction = signal<{ type: string; offer: OfferResponse } | null>(null);
  confirmDialogData = signal<ConfirmDialogData>({
    title: '',
    message: '',
    confirmText: 'Confirm',
    cancelText: 'Cancel',
    variant: 'danger',
    icon: 'exclamation-triangle',
  });

  ngOnInit(): void {
    this.loadOffers();
  }

  loadOffers(page = 0): void {
    const uid = this.userId();
    if (uid) {
      this.store.dispatch(OfferActions.loadMyOffers({ userId: uid, page }));
    }
  }

  openCreate(): void {
    this.editingOffer.set(null);
    this.resetForm();
    this.isModalOpen.set(true);
  }

  openEdit(offer: OfferResponse): void {
    this.editingOffer.set(offer);
    this.formTitle.set(offer.title);
    this.formDescription.set(offer.description);
    this.formContractType.set(offer.contractType);
    this.formLocation.set(offer.location ?? '');
    this.formSalaryRange.set(offer.salaryRange ?? '');
    this.formRequirements.set(offer.requirements ?? '');
    this.formBenefits.set(offer.benefits ?? '');
    this.formExpiresAt.set(offer.expiresAt ?? '');
    this.clearValidation();
    this.isModalOpen.set(true);
  }

  closeModal(): void {
    this.isModalOpen.set(false);
  }

  resetForm(): void {
    this.formTitle.set('');
    this.formDescription.set('');
    this.formContractType.set('');
    this.formLocation.set('');
    this.formSalaryRange.set('');
    this.formRequirements.set('');
    this.formBenefits.set('');
    this.formExpiresAt.set('');
    this.clearValidation();
  }

  clearValidation(): void {
    this.titleError.set('');
    this.descriptionError.set('');
    this.contractTypeError.set('');
  }

  validate(): boolean {
    let valid = true;
    this.clearValidation();

    if (!this.formTitle().trim()) {
      this.titleError.set('Title is required');
      valid = false;
    }
    if (!this.formDescription().trim()) {
      this.descriptionError.set('Description is required');
      valid = false;
    }
    if (!this.formContractType()) {
      this.contractTypeError.set('Contract type is required');
      valid = false;
    }
    return valid;
  }

  saveOffer(): void {
    if (!this.validate()) return;

    const uid = this.userId();
    const editing = this.editingOffer();

    if (editing) {
      const request: UpdateOfferRequest = {
        title: this.formTitle().trim(),
        description: this.formDescription().trim(),
        contractType: this.formContractType() as OfferContractType,
        location: this.formLocation().trim() || undefined,
        salaryRange: this.formSalaryRange().trim() || undefined,
        requirements: this.formRequirements().trim() || undefined,
        benefits: this.formBenefits().trim() || undefined,
        expiresAt: this.formExpiresAt() || undefined,
      };
      this.store.dispatch(OfferActions.updateOffer({ userId: uid, offerId: editing.id, request }));
    } else {
      const request: CreateOfferRequest = {
        title: this.formTitle().trim(),
        description: this.formDescription().trim(),
        contractType: this.formContractType() as OfferContractType,
        location: this.formLocation().trim() || undefined,
        salaryRange: this.formSalaryRange().trim() || undefined,
        requirements: this.formRequirements().trim() || undefined,
        benefits: this.formBenefits().trim() || undefined,
        expiresAt: this.formExpiresAt() || undefined,
      };
      this.store.dispatch(OfferActions.createOffer({ userId: uid, request }));
    }

    this.isModalOpen.set(false);
  }

  // Status actions with confirm dialog
  requestPublish(offer: OfferResponse): void {
    this.confirmAction.set({ type: 'publish', offer });
    this.confirmDialogData.set({
      title: 'Publish Offer',
      message: `Are you sure you want to publish "${offer.title}"? It will be visible to all candidates.`,
      confirmText: 'Publish',
      cancelText: 'Cancel',
      variant: 'primary',
      icon: 'check',
    });
    this.showConfirmDialog.set(true);
  }

  requestClose(offer: OfferResponse): void {
    this.confirmAction.set({ type: 'close', offer });
    this.confirmDialogData.set({
      title: 'Close Offer',
      message: `Are you sure you want to close "${offer.title}"? It will no longer accept applications.`,
      confirmText: 'Close',
      cancelText: 'Cancel',
      variant: 'secondary',
      icon: 'exclamation-triangle',
    });
    this.showConfirmDialog.set(true);
  }

  requestArchive(offer: OfferResponse): void {
    this.confirmAction.set({ type: 'archive', offer });
    this.confirmDialogData.set({
      title: 'Archive Offer',
      message: `Are you sure you want to archive "${offer.title}"?`,
      confirmText: 'Archive',
      cancelText: 'Cancel',
      variant: 'secondary',
      icon: 'exclamation-triangle',
    });
    this.showConfirmDialog.set(true);
  }

  requestDelete(offer: OfferResponse): void {
    this.confirmAction.set({ type: 'delete', offer });
    this.confirmDialogData.set({
      title: 'Delete Offer',
      message: `Are you sure you want to delete "${offer.title}"? This cannot be undone.`,
      confirmText: 'Delete',
      cancelText: 'Cancel',
      variant: 'danger',
      icon: 'exclamation-triangle',
    });
    this.showConfirmDialog.set(true);
  }

  confirmDialogAction(): void {
    const action = this.confirmAction();
    if (!action) return;
    const uid = this.userId();

    switch (action.type) {
      case 'publish':
        this.store.dispatch(OfferActions.publishOffer({ userId: uid, offerId: action.offer.id }));
        break;
      case 'close':
        this.store.dispatch(OfferActions.closeOffer({ userId: uid, offerId: action.offer.id }));
        break;
      case 'archive':
        this.store.dispatch(OfferActions.archiveOffer({ userId: uid, offerId: action.offer.id }));
        break;
      case 'delete':
        this.store.dispatch(OfferActions.deleteOffer({ userId: uid, offerId: action.offer.id }));
        break;
    }

    this.showConfirmDialog.set(false);
    this.confirmAction.set(null);
  }

  cancelConfirm(): void {
    this.showConfirmDialog.set(false);
    this.confirmAction.set(null);
  }

  clearError(): void {
    this.store.dispatch(OfferActions.clearError());
  }

  goToPage(page: number): void {
    this.loadOffers(page);
  }

  get pages(): number[] {
    const total = this.totalPages();
    return Array.from({ length: total }, (_, i) => i);
  }
}




