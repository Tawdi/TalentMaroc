import { Component, input, output, signal, inject, ChangeDetectionStrategy } from '@angular/core';
import { UiCard, UiButton, UiBadge, UiModal, UiAlert, IconComponent, UiAutocomplete, AutocompleteOption, UiConfirmDialog, ConfirmDialogData } from '../../../../shared';
import { UiSelect } from '../../../../shared/components/ui-select/ui-select';
import { CandidateProfileService } from '../../../../core';
import { Skill, SKILL_LEVEL_LABELS, SKILL_LEVEL_OPTIONS, SkillLevel } from '../../../../core/models/profile.model';
import { COMMON_SKILLS } from '../../../../core/constants/skills.constant';

@Component({
  selector: 'app-skills-section',
  standalone: true,
  imports: [UiCard, UiButton, UiBadge, UiModal, UiAlert, UiSelect, IconComponent, UiAutocomplete, UiConfirmDialog],
  templateUrl: './skills-section.html',
  styleUrl: './skills-section.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SkillsSectionComponent {
  private readonly profileService = inject(CandidateProfileService);

  skills = input<Skill[]>([]);
  userId = input.required<string>();
  compact = input(false);
  changed = output<void>();

  isModalOpen = signal(false);
  editingSkill = signal<Skill | null>(null);
  saving = signal(false);
  error = signal<string | null>(null);

  // Confirmation dialog state
  showConfirmDialog = signal(false);
  skillToDelete = signal<Skill | null>(null);
  confirmDialogData = signal<ConfirmDialogData>({
    title: 'Remove Skill',
    message: '',
    confirmText: 'Remove',
    cancelText: 'Cancel',
    variant: 'danger',
    icon: 'exclamation-triangle'
  });

  skillName = signal('');
  skillLevel = signal('');

  readonly levelLabels = SKILL_LEVEL_LABELS;
  readonly levelOptions = SKILL_LEVEL_OPTIONS.map(o => ({ value: o.value, label: o.label }));
  readonly skillOptions: AutocompleteOption[] = COMMON_SKILLS.map(s => ({ value: s, label: s }));

  getLevelVariant(level: SkillLevel): 'info' | 'warning' | 'success' | 'primary' {
    const map: Record<SkillLevel, 'info' | 'warning' | 'success' | 'primary'> = {
      BEGINNER: 'info', INTERMEDIATE: 'warning', ADVANCED: 'success', EXPERT: 'primary',
    };
    return map[level] ?? 'info';
  }

  openAdd(): void {
    this.editingSkill.set(null);
    this.skillName.set('');
    this.skillLevel.set('');
    this.error.set(null);
    this.isModalOpen.set(true);
  }

  openEdit(skill: Skill): void {
    this.editingSkill.set(skill);
    this.skillName.set(skill.name);
    this.skillLevel.set(skill.level);
    this.error.set(null);
    this.isModalOpen.set(true);
  }

  close(): void {
    this.isModalOpen.set(false);
  }

  save(): void {
    if (!this.skillName() || !this.skillLevel()) {
      this.error.set('Name and level are required.');
      return;
    }

    this.saving.set(true);
    this.error.set(null);

    const payload: Skill = {
      name: this.skillName(),
      level: this.skillLevel() as SkillLevel,
    };

    const editing = this.editingSkill();
    const obs = editing
      ? this.profileService.updateSkill(this.userId(), editing.id!, payload)
      : this.profileService.createSkill(this.userId(), payload);

    obs.subscribe({
      next: () => {
        this.saving.set(false);
        this.isModalOpen.set(false);
        this.changed.emit();
      },
      error: (err) => {
        this.saving.set(false);
        this.error.set(err?.error?.message || 'Failed to save skill.');
      },
    });
  }

  delete(skill: Skill): void {
    this.skillToDelete.set(skill);
    this.confirmDialogData.set({
      title: 'Remove Skill',
      message: `Are you sure you want to remove "${skill.name}" from your skills?`,
      confirmText: 'Remove',
      cancelText: 'Cancel',
      variant: 'danger',
      icon: 'exclamation-triangle'
    });
    this.showConfirmDialog.set(true);
  }

  confirmDelete(): void {
    const skill = this.skillToDelete();
    if (skill) {
      this.profileService.deleteSkill(this.userId(), skill.id!).subscribe({
        next: () => {
          this.showConfirmDialog.set(false);
          this.skillToDelete.set(null);
          this.changed.emit();
        },
        error: () => {
          this.showConfirmDialog.set(false);
          this.skillToDelete.set(null);
        }
      });
    }
  }

  cancelDelete(): void {
    this.showConfirmDialog.set(false);
    this.skillToDelete.set(null);
  }
}

