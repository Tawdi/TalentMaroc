import { Component, inject, computed, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { Store } from '@ngrx/store';
import { DatePipe, SlicePipe } from '@angular/common';
import {
  UiCard, UiButton, UiAlert, UiBadge, IconComponent, Skeleton,
  UiConfirmDialog, ConfirmDialogData,
} from '../../../../shared';
import { AuthService } from '../../../../core/services/auth.service';
import {
  APPLICATION_STATUS_LABELS,
  getApplicationStatusVariant,
  ApplicationResponse,
} from '../../../../core/models/application.model';
import {
  OFFER_CONTRACT_TYPE_LABELS,
} from '../../../../core/models/company-offers.model';
import {
  ApplicationActions,
  selectMyApplications,
  selectMyLoading,
  selectMyTotalElements,
  selectMyTotalPages,
  selectMyCurrentPage,
  selectSaving,
  selectApplicationError,
} from '../../store';
import { signal } from '@angular/core';

@Component({
  selector: 'app-my-applications',
  standalone: true,
  imports: [
    UiCard, UiButton, UiAlert, UiBadge, IconComponent, Skeleton,
    UiConfirmDialog, DatePipe, SlicePipe,
  ],
  templateUrl: './my-applications.html',
  styleUrl: './my-applications.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MyApplicationsComponent implements OnInit {
  private readonly store = inject(Store);
  private readonly authService = inject(AuthService);

  readonly userId = computed(() => this.authService.currentUser()?.id ?? '');
  readonly applications = this.store.selectSignal(selectMyApplications);
  readonly loading = this.store.selectSignal(selectMyLoading);
  readonly totalElements = this.store.selectSignal(selectMyTotalElements);
  readonly totalPages = this.store.selectSignal(selectMyTotalPages);
  readonly currentPage = this.store.selectSignal(selectMyCurrentPage);
  readonly saving = this.store.selectSignal(selectSaving);
  readonly error = this.store.selectSignal(selectApplicationError);

  readonly statusLabels = APPLICATION_STATUS_LABELS;
  readonly contractLabels: Record<string, string> = OFFER_CONTRACT_TYPE_LABELS as Record<string, string>;
  readonly getStatusVariant = getApplicationStatusVariant;

  // Confirm dialog
  showConfirmDialog = signal(false);
  confirmTarget = signal<ApplicationResponse | null>(null);
  confirmDialogData = signal<ConfirmDialogData>({
    title: '',
    message: '',
    confirmText: 'Confirm',
    cancelText: 'Cancel',
    variant: 'danger',
    icon: 'exclamation-triangle',
  });

  ngOnInit(): void {
    this.loadApplications();
  }

  loadApplications(page = 0): void {
    const uid = this.userId();
    if (uid) {
      this.store.dispatch(ApplicationActions.loadMyApplications({ userId: uid, page }));
    }
  }

  requestWithdraw(app: ApplicationResponse): void {
    this.confirmTarget.set(app);
    this.confirmDialogData.set({
      title: 'Withdraw Application',
      message: `Are you sure you want to withdraw your application for "${app.offer?.title ?? 'this position'}"?`,
      confirmText: 'Withdraw',
      cancelText: 'Cancel',
      variant: 'danger',
      icon: 'exclamation-triangle',
    });
    this.showConfirmDialog.set(true);
  }

  confirmWithdraw(): void {
    const app = this.confirmTarget();
    if (!app) return;
    this.store.dispatch(
      ApplicationActions.withdrawApplication({ userId: this.userId(), applicationId: app.id }),
    );
    this.showConfirmDialog.set(false);
    this.confirmTarget.set(null);
  }

  cancelConfirm(): void {
    this.showConfirmDialog.set(false);
    this.confirmTarget.set(null);
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

  canWithdraw(app: ApplicationResponse): boolean {
    return app.status === 'RECEIVED' || app.status === 'REVIEWING';
  }
}
