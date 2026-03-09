import { Component, input, output, signal, inject, ChangeDetectionStrategy } from '@angular/core';
import { UiCard, UiButton, UiBadge, UiInput, UiModal, UiAlert, UiDateInput, IconComponent, UiConfirmDialog, ConfirmDialogData } from '../../../../shared';
import { UiSelect } from '../../../../shared/components/ui-select/ui-select';
import { UiTextarea } from '../../../../shared/components/ui-textarea/ui-textarea';
import { CandidateProfileService } from '../../../../core';
import { Experience, CONTRACT_TYPE_LABELS, CONTRACT_TYPE_OPTIONS, ContractType } from '../../../../core/models/profile.model';

@Component({
  selector: 'app-experiences-section',
  standalone: true,
  imports: [UiCard, UiButton, UiBadge, UiInput, UiModal, UiAlert, UiSelect, UiTextarea, UiDateInput, IconComponent, UiConfirmDialog],
  templateUrl: './experiences-section.html',
  styleUrl: './experiences-section.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ExperiencesSectionComponent {
  private readonly profileService = inject(CandidateProfileService);

  experiences = input<Experience[]>([]);
  userId = input.required<string>();
  changed = output<void>();

  isModalOpen = signal(false);
  editingExp = signal<Experience | null>(null);
  saving = signal(false);
  error = signal<string | null>(null);

  // Confirmation dialog state
  showConfirmDialog = signal(false);
  expToDelete = signal<Experience | null>(null);
  confirmDialogData = signal<ConfirmDialogData>({
    title: 'Remove Experience',
    message: '',
    confirmText: 'Remove',
    cancelText: 'Cancel',
    variant: 'danger',
    icon: 'exclamation-triangle'
  });

  title = signal('');
  company = signal('');
  location = signal('');
  description = signal('');
  contractType = signal('');
  startDate = signal('');
  endDate = signal('');
  currentJob = signal(false);

  readonly contractLabels = CONTRACT_TYPE_LABELS;
  readonly contractOptions = CONTRACT_TYPE_OPTIONS.map(o => ({ value: o.value, label: o.label }));

  openAdd(): void {
    this.editingExp.set(null);
    this.title.set(''); this.company.set(''); this.location.set('');
    this.description.set(''); this.contractType.set('');
    this.startDate.set(''); this.endDate.set(''); this.currentJob.set(false);
    this.error.set(null);
    this.isModalOpen.set(true);
  }

  openEdit(exp: Experience): void {
    this.editingExp.set(exp);
    this.title.set(exp.title); this.company.set(exp.company);
    this.location.set(exp.location ?? ''); this.description.set(exp.description ?? '');
    this.contractType.set(exp.contractType); this.startDate.set(exp.startDate);
    this.endDate.set(exp.endDate ?? ''); this.currentJob.set(exp.currentJob);
    this.error.set(null);
    this.isModalOpen.set(true);
  }

  close(): void { this.isModalOpen.set(false); }

  toggleCurrentJob(event: Event): void {
    this.currentJob.set((event.target as HTMLInputElement).checked);
    if (this.currentJob()) this.endDate.set('');
  }

  save(): void {
    if (!this.title() || !this.company() || !this.contractType() || !this.startDate()) {
      this.error.set('Title, company, contract type, and start date are required.');
      return;
    }
    this.saving.set(true);
    this.error.set(null);

    const payload: Experience = {
      title: this.title(), company: this.company(),
      location: this.location() || undefined, description: this.description() || undefined,
      contractType: this.contractType() as ContractType,
      startDate: this.startDate(),
      endDate: this.currentJob() ? null : (this.endDate() || null),
      currentJob: this.currentJob(),
    };

    const editing = this.editingExp();
    const obs = editing
      ? this.profileService.updateExperience(this.userId(), editing.id!, payload)
      : this.profileService.createExperience(this.userId(), payload);

    obs.subscribe({
      next: () => { this.saving.set(false); this.isModalOpen.set(false); this.changed.emit(); },
      error: (err) => { this.saving.set(false); this.error.set(err?.error?.message || 'Failed to save experience.'); },
    });
  }

  delete(exp: Experience): void {
    this.expToDelete.set(exp);
    this.confirmDialogData.set({
      title: 'Remove Experience',
      message: `Are you sure you want to remove "${exp.title}" at ${exp.company} from your experience?`,
      confirmText: 'Remove',
      cancelText: 'Cancel',
      variant: 'danger',
      icon: 'exclamation-triangle'
    });
    this.showConfirmDialog.set(true);
  }

  confirmDelete(): void {
    const exp = this.expToDelete();
    if (exp) {
      this.profileService.deleteExperience(this.userId(), exp.id!).subscribe({
        next: () => {
          this.showConfirmDialog.set(false);
          this.expToDelete.set(null);
          this.changed.emit();
        },
        error: () => {
          this.showConfirmDialog.set(false);
          this.expToDelete.set(null);
        }
      });
    }
  }

  cancelDelete(): void {
    this.showConfirmDialog.set(false);
    this.expToDelete.set(null);
  }

  formatDate(date: string): string {
    if (!date) return '';
    const d = new Date(date);
    return d.toLocaleDateString('en-US', { month: 'short', year: 'numeric' });
  }
}

