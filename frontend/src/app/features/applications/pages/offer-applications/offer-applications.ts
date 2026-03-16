import { Component, inject, computed, signal, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { DatePipe, SlicePipe } from '@angular/common';
import {
  UiCard, UiButton, UiAlert, UiBadge, UiSelect, UiModal, UiTextarea,
  IconComponent, Skeleton, UiConfirmDialog, ConfirmDialogData,
} from '../../../../shared';
import { AuthService } from '../../../../core/services/auth.service';
import {
  ApplicationResponse,
  ApplicationStatus,
  APPLICATION_STATUS_LABELS,
  APPLICATION_STATUS_OPTIONS,
  getApplicationStatusVariant,
} from '../../../../core/models/application.model';
import {
  ApplicationActions,
  selectOfferApplications,
  selectOfferLoading,
  selectOfferTotalElements,
  selectOfferTotalPages,
  selectOfferCurrentPage,
  selectSaving,
  selectApplicationError,
} from '../../store';

@Component({
  selector: 'app-offer-applications',
  standalone: true,
  imports: [
    UiCard, UiButton, UiAlert, UiBadge, UiSelect, UiModal, UiTextarea,
    IconComponent, Skeleton, UiConfirmDialog, DatePipe, SlicePipe,
  ],
  templateUrl: './offer-applications.html',
  styleUrl: './offer-applications.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OfferApplicationsComponent implements OnInit {
  private readonly store = inject(Store);
  private readonly authService = inject(AuthService);
  private readonly route = inject(ActivatedRoute);

  readonly userId = computed(() => this.authService.currentUser()?.id ?? '');
  readonly offerId = Number(this.route.snapshot.paramMap.get('offerId'));

  readonly applications = this.store.selectSignal(selectOfferApplications);
  readonly loading = this.store.selectSignal(selectOfferLoading);
  readonly totalElements = this.store.selectSignal(selectOfferTotalElements);
  readonly totalPages = this.store.selectSignal(selectOfferTotalPages);
  readonly currentPage = this.store.selectSignal(selectOfferCurrentPage);
  readonly saving = this.store.selectSignal(selectSaving);
  readonly error = this.store.selectSignal(selectApplicationError);

  readonly statusLabels = APPLICATION_STATUS_LABELS;
  readonly getStatusVariant = getApplicationStatusVariant;
  readonly statusFilterOptions = [
    { value: '', label: 'All Statuses' },
    ...APPLICATION_STATUS_OPTIONS,
  ];

  // Filter
  statusFilter = signal('');

  // Status update modal
  showStatusModal = signal(false);
  selectedApp = signal<ApplicationResponse | null>(null);
  newStatus = signal('');
  companyNote = signal('');

  // Status options for the update modal (transitions only)
  readonly statusUpdateOptions = [
    { value: 'REVIEWING', label: 'Under Review' },
    { value: 'ACCEPTED', label: 'Accepted' },
    { value: 'REJECTED', label: 'Rejected' },
  ];

  // Confirm dialog
  showConfirmDialog = signal(false);
  confirmDialogData = signal<ConfirmDialogData>({
    title: '',
    message: '',
    confirmText: 'Confirm',
    cancelText: 'Cancel',
    variant: 'primary',
    icon: 'check',
  });

  ngOnInit(): void {
    this.loadApplications();
  }

  loadApplications(page = 0): void {
    const uid = this.userId();
    if (uid && this.offerId) {
      const status = this.statusFilter() as ApplicationStatus | undefined;
      this.store.dispatch(
        ApplicationActions.loadOfferApplications({
          userId: uid,
          offerId: this.offerId,
          status: status || undefined,
          page,
        }),
      );
    }
  }

  onFilterChange(value: string): void {
    this.statusFilter.set(value);
    this.loadApplications();
  }

  openStatusUpdate(app: ApplicationResponse): void {
    this.selectedApp.set(app);
    this.newStatus.set('');
    this.companyNote.set('');
    this.showStatusModal.set(true);
  }

  closeStatusModal(): void {
    this.showStatusModal.set(false);
    this.selectedApp.set(null);
  }

  submitStatusUpdate(): void {
    const app = this.selectedApp();
    const status = this.newStatus();
    if (!app || !status) return;

    this.confirmDialogData.set({
      title: `Update to ${this.statusLabels[status as ApplicationStatus]}`,
      message: `Change this application's status to "${this.statusLabels[status as ApplicationStatus]}"?`,
      confirmText: 'Update',
      cancelText: 'Cancel',
      variant: 'primary',
      icon: 'check',
    });
    this.showStatusModal.set(false);
    this.showConfirmDialog.set(true);
  }

  confirmStatusUpdate(): void {
    const app = this.selectedApp();
    const status = this.newStatus() as ApplicationStatus;
    if (!app || !status) return;

    this.store.dispatch(
      ApplicationActions.updateApplicationStatus({
        userId: this.userId(),
        applicationId: app.id,
        request: {
          status,
          companyNote: this.companyNote().trim() || undefined,
        },
      }),
    );
    this.showConfirmDialog.set(false);
    this.selectedApp.set(null);
  }

  cancelConfirm(): void {
    this.showConfirmDialog.set(false);
  }

  clearError(): void {
    this.store.dispatch(ApplicationActions.clearError());
  }

  goToPage(page: number): void {
    this.loadApplications(page);
  }

  get pages(): number[] {
    return Array.from({ length: this.totalPages() }, (_, i) => i);
  }

  canUpdateStatus(app: ApplicationResponse): boolean {
    return app.status !== 'ACCEPTED' && app.status !== 'REJECTED' && app.status !== 'WITHDRAWN';
  }
}
