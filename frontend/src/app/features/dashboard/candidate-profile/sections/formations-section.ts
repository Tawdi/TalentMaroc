import { Component, input, output, signal, inject, ChangeDetectionStrategy } from '@angular/core';
import { UiCard, UiButton, UiInput, UiModal, UiAlert, UiDateInput, IconComponent, UiConfirmDialog, ConfirmDialogData } from '../../../../shared';
import { UiTextarea } from '../../../../shared/components/ui-textarea/ui-textarea';
import { CandidateProfileService } from '../../../../core';
import { Formation } from '../../../../core/models/profile.model';

@Component({
  selector: 'app-formations-section',
  standalone: true,
  imports: [UiCard, UiButton, UiInput, UiModal, UiAlert, UiTextarea, UiDateInput, IconComponent, UiConfirmDialog],
  templateUrl: './formations-section.html',
  styleUrl: './formations-section.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FormationsSectionComponent {
  private readonly profileService = inject(CandidateProfileService);

  formations = input<Formation[]>([]);
  userId = input.required<string>();
  changed = output<void>();

  isModalOpen = signal(false);
  editingForm = signal<Formation | null>(null);
  saving = signal(false);
  error = signal<string | null>(null);

  // Confirmation dialog state
  showConfirmDialog = signal(false);
  formToDelete = signal<Formation | null>(null);
  confirmDialogData = signal<ConfirmDialogData>({
    title: 'Remove Education',
    message: '',
    confirmText: 'Remove',
    cancelText: 'Cancel',
    variant: 'danger',
    icon: 'exclamation-triangle'
  });

  institution = signal('');
  degree = signal('');
  fieldOfStudy = signal('');
  description = signal('');
  startDate = signal('');
  endDate = signal('');
  currentlyStudying = signal(false);

  openAdd(): void {
    this.editingForm.set(null);
    this.institution.set(''); this.degree.set(''); this.fieldOfStudy.set('');
    this.description.set(''); this.startDate.set(''); this.endDate.set('');
    this.currentlyStudying.set(false); this.error.set(null);
    this.isModalOpen.set(true);
  }

  openEdit(f: Formation): void {
    this.editingForm.set(f);
    this.institution.set(f.institution); this.degree.set(f.degree);
    this.fieldOfStudy.set(f.fieldOfStudy ?? ''); this.description.set(f.description ?? '');
    this.startDate.set(f.startDate); this.endDate.set(f.endDate ?? '');
    this.currentlyStudying.set(f.currentlyStudying); this.error.set(null);
    this.isModalOpen.set(true);
  }

  close(): void { this.isModalOpen.set(false); }

  toggleCurrentlyStudying(event: Event): void {
    this.currentlyStudying.set((event.target as HTMLInputElement).checked);
    if (this.currentlyStudying()) this.endDate.set('');
  }

  save(): void {
    if (!this.institution() || !this.degree() || !this.startDate()) {
      this.error.set('Institution, degree, and start date are required.');
      return;
    }
    this.saving.set(true);
    this.error.set(null);

    const payload: Formation = {
      institution: this.institution(), degree: this.degree(),
      fieldOfStudy: this.fieldOfStudy() || undefined,
      description: this.description() || undefined,
      startDate: this.startDate(),
      endDate: this.currentlyStudying() ? null : (this.endDate() || null),
      currentlyStudying: this.currentlyStudying(),
    };

    const editing = this.editingForm();
    const obs = editing
      ? this.profileService.updateFormation(this.userId(), editing.id!, payload)
      : this.profileService.createFormation(this.userId(), payload);

    obs.subscribe({
      next: () => { this.saving.set(false); this.isModalOpen.set(false); this.changed.emit(); },
      error: (err) => { this.saving.set(false); this.error.set(err?.error?.message || 'Failed to save formation.'); },
    });
  }

  delete(f: Formation): void {
    this.formToDelete.set(f);
    this.confirmDialogData.set({
      title: 'Remove Education',
      message: `Are you sure you want to remove "${f.degree}" at ${f.institution} from your education?`,
      confirmText: 'Remove',
      cancelText: 'Cancel',
      variant: 'danger',
      icon: 'exclamation-triangle'
    });
    this.showConfirmDialog.set(true);
  }

  confirmDelete(): void {
    const f = this.formToDelete();
    if (f) {
      this.profileService.deleteFormation(this.userId(), f.id!).subscribe({
        next: () => {
          this.showConfirmDialog.set(false);
          this.formToDelete.set(null);
          this.changed.emit();
        },
        error: () => {
          this.showConfirmDialog.set(false);
          this.formToDelete.set(null);
        }
      });
    }
  }

  cancelDelete(): void {
    this.showConfirmDialog.set(false);
    this.formToDelete.set(null);
  }

  formatDate(date: string): string {
    if (!date) return '';
    const d = new Date(date);
    return d.toLocaleDateString('en-US', { month: 'short', year: 'numeric' });
  }
}

