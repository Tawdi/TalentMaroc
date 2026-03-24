import { Component, input, output, signal, inject, ChangeDetectionStrategy } from '@angular/core';
import { UiCard, UiButton, UiAlert, IconComponent, UiConfirmDialog, ConfirmDialogData } from '../../../../shared';
import { CandidateProfileService } from '../../../../core';
import { CandidateProfile } from '../../../../core/models/profile.model';

@Component({
  selector: 'app-cv-section',
  standalone: true,
  imports: [UiCard, UiButton, UiAlert, IconComponent, UiConfirmDialog],
  templateUrl: './cv-section.html',
  styleUrl: './cv-section.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CvSectionComponent {
  private readonly profileService = inject(CandidateProfileService);

  profile = input.required<CandidateProfile>();
  userId = input.required<string>();
  changed = output<void>();

  uploading = signal(false);
  error = signal<string | null>(null);
  success = signal<string | null>(null);

  // Confirmation dialog state
  showConfirmDialog = signal(false);
  confirmDialogData = signal<ConfirmDialogData>({
    title: 'Delete CV',
    message: 'Are you sure you want to delete your CV? This action cannot be undone.',
    confirmText: 'Delete',
    cancelText: 'Cancel',
    variant: 'danger',
    icon: 'exclamation-triangle'
  });

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    // Validate file type
    if (file.type !== 'application/pdf') {
      this.error.set('Only PDF files are allowed.');
      return;
    }

    // Validate file size (10MB)
    if (file.size > 10 * 1024 * 1024) {
      this.error.set('File size must be less than 10MB.');
      return;
    }

    this.uploading.set(true);
    this.error.set(null);
    this.success.set(null);

    this.profileService.uploadCv(this.userId(), file).subscribe({
      next: () => {
        this.uploading.set(false);
        this.success.set('CV uploaded successfully!');
        this.changed.emit();
        // Reset the file input
        input.value = '';
      },
      error: (err) => {
        this.uploading.set(false);
        this.error.set(err?.error?.message || 'Failed to upload CV.');
        input.value = '';
      },
    });
  }

  download(): void {
    this.profileService.downloadCv(this.userId()).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = this.profile().cvOriginalName || 'cv.pdf';
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: () => this.error.set('Failed to download CV.'),
    });
  }

  requestDelete(): void {
    this.showConfirmDialog.set(true);
  }

  confirmDelete(): void {
    this.showConfirmDialog.set(false);
    this.profileService.deleteCv(this.userId()).subscribe({
      next: () => {
        this.success.set('CV deleted.');
        this.changed.emit();
      },
      error: () => this.error.set('Failed to delete CV.'),
    });
  }

  cancelDelete(): void {
    this.showConfirmDialog.set(false);
  }

  deleteCv(): void {
    this.requestDelete();
  }
}
