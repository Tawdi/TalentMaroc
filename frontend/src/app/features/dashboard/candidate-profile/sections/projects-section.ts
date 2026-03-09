import { Component, input, output, signal, inject, ChangeDetectionStrategy } from '@angular/core';
import { UiCard, UiButton, UiBadge, UiInput, UiModal, UiAlert, UiDateInput, IconComponent, UiConfirmDialog, ConfirmDialogData } from '../../../../shared';
import { UiTextarea } from '../../../../shared/components/ui-textarea/ui-textarea';
import { CandidateProfileService } from '../../../../core';
import { Project } from '../../../../core/models/profile.model';

@Component({
  selector: 'app-projects-section',
  standalone: true,
  imports: [UiCard, UiButton, UiBadge, UiInput, UiModal, UiAlert, UiTextarea, UiDateInput, IconComponent, UiConfirmDialog],
  templateUrl: './projects-section.html',
  styleUrl: './projects-section.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProjectsSectionComponent {
  private readonly profileService = inject(CandidateProfileService);

  projects = input<Project[]>([]);
  userId = input.required<string>();
  changed = output<void>();

  isModalOpen = signal(false);
  editingProject = signal<Project | null>(null);
  saving = signal(false);
  error = signal<string | null>(null);

  // Confirmation dialog state
  showConfirmDialog = signal(false);
  projectToDelete = signal<Project | null>(null);
  confirmDialogData = signal<ConfirmDialogData>({
    title: 'Remove Project',
    message: '',
    confirmText: 'Remove',
    cancelText: 'Cancel',
    variant: 'danger',
    icon: 'exclamation-triangle'
  });

  title = signal('');
  description = signal('');
  url = signal('');
  technologies = signal('');
  startDate = signal('');
  endDate = signal('');

  openAdd(): void {
    this.editingProject.set(null);
    this.title.set(''); this.description.set(''); this.url.set('');
    this.technologies.set(''); this.startDate.set(''); this.endDate.set('');
    this.error.set(null);
    this.isModalOpen.set(true);
  }

  openEdit(p: Project): void {
    this.editingProject.set(p);
    this.title.set(p.title); this.description.set(p.description ?? '');
    this.url.set(p.url ?? ''); this.technologies.set(p.technologies ?? '');
    this.startDate.set(p.startDate ?? ''); this.endDate.set(p.endDate ?? '');
    this.error.set(null);
    this.isModalOpen.set(true);
  }

  close(): void { this.isModalOpen.set(false); }

  save(): void {
    if (!this.title()) {
      this.error.set('Project title is required.');
      return;
    }
    this.saving.set(true);
    this.error.set(null);

    const payload: Project = {
      title: this.title(),
      description: this.description() || undefined,
      url: this.url() || undefined,
      technologies: this.technologies() || undefined,
      startDate: this.startDate() || undefined,
      endDate: this.endDate() || null,
    };

    const editing = this.editingProject();
    const obs = editing
      ? this.profileService.updateProject(this.userId(), editing.id!, payload)
      : this.profileService.createProject(this.userId(), payload);

    obs.subscribe({
      next: () => { this.saving.set(false); this.isModalOpen.set(false); this.changed.emit(); },
      error: (err) => { this.saving.set(false); this.error.set(err?.error?.message || 'Failed to save project.'); },
    });
  }

  delete(p: Project): void {
    this.projectToDelete.set(p);
    this.confirmDialogData.set({
      title: 'Remove Project',
      message: `Are you sure you want to remove project "${p.title}" from your portfolio?`,
      confirmText: 'Remove',
      cancelText: 'Cancel',
      variant: 'danger',
      icon: 'exclamation-triangle'
    });
    this.showConfirmDialog.set(true);
  }

  confirmDelete(): void {
    const p = this.projectToDelete();
    if (p) {
      this.profileService.deleteProject(this.userId(), p.id!).subscribe({
        next: () => {
          this.showConfirmDialog.set(false);
          this.projectToDelete.set(null);
          this.changed.emit();
        },
        error: () => {
          this.showConfirmDialog.set(false);
          this.projectToDelete.set(null);
        }
      });
    }
  }

  cancelDelete(): void {
    this.showConfirmDialog.set(false);
    this.projectToDelete.set(null);
  }

  getTechBadges(tech?: string): string[] {
    if (!tech) return [];
    return tech.split(',').map(t => t.trim()).filter(t => t);
  }
}

