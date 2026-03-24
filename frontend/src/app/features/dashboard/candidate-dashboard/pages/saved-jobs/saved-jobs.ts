import { Component, ChangeDetectionStrategy, OnInit, signal, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import {
  UiCard, UiButton, UiBadge, UiAlert, IconComponent,
  UiConfirmDialog, ConfirmDialogData,
  Skeleton,
} from '../../../../../shared';
import { OFFER_CONTRACT_TYPE_LABELS } from '../../../../../core/models/company-offers.model';
import { AuthService } from '../../../../../core/services/auth.service';
import { SavedJobsService } from '../../../../../core/services/saved-jobs.service';
import { SavedJob as SavedJobDto } from '../../../../../core/models/saved-jobs.model';

type SavedJob = SavedJobDto & {
  title?: string;
  companyName?: string;
  location?: string;
  contractType?: string;
};

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
  private readonly savedJobsService = inject(SavedJobsService);

  readonly savedJobs = signal<SavedJob[]>([]);
  readonly error = signal<string | null>(null);
  readonly loading = signal(false);

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
    this.loading.set(true);
    this.error.set(null);
    this.savedJobsService.getSavedJobs(uid).subscribe({
      next: (jobs) => {
        this.savedJobs.set(jobs);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.message || 'Unable to load saved jobs.');
        this.loading.set(false);
      },
    });
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

    this.savedJobsService.removeJob(uid, job.offerId).subscribe({
      next: () => {
        const next = this.savedJobs().filter((j) => j.id !== job.id);
        this.savedJobs.set(next);
        this.showConfirmDialog.set(false);
        this.confirmTarget.set(null);
      },
      error: (err) => {
        this.error.set(err?.error?.message || 'Failed to remove saved job.');
        this.showConfirmDialog.set(false);
        this.confirmTarget.set(null);
      },
    });
  }

  cancelRemove(): void {
    this.showConfirmDialog.set(false);
    this.confirmTarget.set(null);
  }

  clearError(): void {
    this.error.set(null);
  }
}
