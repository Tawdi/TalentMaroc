import { Component, ChangeDetectionStrategy, OnInit, signal, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { Store } from '@ngrx/store';
import {
  UiCard, UiButton, UiBadge, UiAlert, IconComponent,
  UiConfirmDialog, ConfirmDialogData,
  Skeleton,
} from '../../../../../shared';
import { OFFER_CONTRACT_TYPE_LABELS } from '../../../../../core/models/company-offers.model';
import { AuthService } from '../../../../../core/services/auth.service';
import { SavedJob } from '../../../../../core/models/saved-jobs.model';
import {
  SavedJobsActions,
  selectSavedJobs,
  selectSavedJobsLoading,
  selectSavedJobsError,
  selectSavedJobsPendingOfferIds,
} from '../../store';

@Component({
  selector: 'app-saved-jobs',
  standalone: true,
  imports: [
    UiCard, UiButton, UiBadge, UiAlert, IconComponent,
    UiConfirmDialog, RouterLink, DatePipe, Skeleton,
  ],
  templateUrl: './saved-jobs.html',
  styleUrl: './saved-jobs.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SavedJobsComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly store = inject(Store);

  readonly savedJobs = this.store.selectSignal(selectSavedJobs);
  readonly error = this.store.selectSignal(selectSavedJobsError);
  readonly loading = this.store.selectSignal(selectSavedJobsLoading);
  readonly pendingOfferIds = this.store.selectSignal(selectSavedJobsPendingOfferIds);

  readonly contractLabels: Record<string, string> = OFFER_CONTRACT_TYPE_LABELS as Record<string, string>;

  // Confirm dialog
  showConfirmDialog = signal(false);
  confirmTarget = signal<SavedJob | null>(null);
  confirmDialogData = signal<ConfirmDialogData>({
    title: 'Remove Saved Job',
    message: 'Are you sure you want to remove this saved job?',
    confirmText: 'Remove',
    cancelText: 'Cancel',
    variant: 'danger',
    icon: 'trash',
  });

  ngOnInit(): void {
    this.loadSavedJobs();
  }

  private get userId(): string {
    return this.authService.currentUser()?.id ?? '';
  }

  loadSavedJobs(): void {
    const uid = this.userId;
    if (!uid) return;
    this.store.dispatch(SavedJobsActions.loadSavedJobs({ userId: uid }));
  }

  requestRemove(job: SavedJob): void {
    this.confirmTarget.set(job);
    this.confirmDialogData.set({
      title: 'Remove Saved Job',
      message: `Remove "${job.title}" from your saved jobs?`,
      confirmText: 'Remove',
      cancelText: 'Cancel',
      variant: 'danger',
      icon: 'trash',
    });
    this.showConfirmDialog.set(true);
  }

  confirmRemove(): void {
    const job = this.confirmTarget();
    if (!job) return;
    const uid = this.userId;
    if (!uid) return;

    this.store.dispatch(SavedJobsActions.removeJob({ userId: uid, offerId: job.offerId }));
    this.showConfirmDialog.set(false);
    this.confirmTarget.set(null);
  }

  cancelRemove(): void {
    this.showConfirmDialog.set(false);
    this.confirmTarget.set(null);
  }

  clearError(): void {
    this.store.dispatch(SavedJobsActions.clearSavedJobsError());
  }

  isPending(offerId: number): boolean {
    return this.pendingOfferIds().includes(offerId);
  }
}
