import { Component, input, output, signal, inject, ChangeDetectionStrategy } from '@angular/core';
import { UiCard, UiButton, UiBadge, UiModal, UiAlert, IconComponent, UiAutocomplete, AutocompleteOption, UiConfirmDialog, ConfirmDialogData } from '../../../../shared';
import { UiSelect } from '../../../../shared/components/ui-select/ui-select';
import { CandidateProfileService } from '../../../../core';
import { SpokenLanguage, PROFICIENCY_LABELS, PROFICIENCY_OPTIONS, LanguageProficiency } from '../../../../core/models/profile.model';
import { WORLD_LANGUAGES } from '../../../../core/constants/languages.constant';

@Component({
  selector: 'app-languages-section',
  standalone: true,
  imports: [UiCard, UiButton, UiBadge, UiModal, UiAlert, UiSelect, IconComponent, UiAutocomplete, UiConfirmDialog],
  templateUrl: './languages-section.html',
  styleUrl: './languages-section.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LanguagesSectionComponent {
  private readonly profileService = inject(CandidateProfileService);

  languages = input<SpokenLanguage[]>([]);
  userId = input.required<string>();
  compact = input(false);
  changed = output<void>();

  isModalOpen = signal(false);
  editingLang = signal<SpokenLanguage | null>(null);
  saving = signal(false);
  error = signal<string | null>(null);

  // Confirmation dialog state
  showConfirmDialog = signal(false);
  langToDelete = signal<SpokenLanguage | null>(null);
  confirmDialogData = signal<ConfirmDialogData>({
    title: 'Remove Language',
    message: '',
    confirmText: 'Remove',
    cancelText: 'Cancel',
    variant: 'danger',
    icon: 'exclamation-triangle'
  });

  langName = signal('');
  langProficiency = signal('');

  readonly profLabels = PROFICIENCY_LABELS;
  readonly profOptions = PROFICIENCY_OPTIONS.map(o => ({ value: o.value, label: o.label }));
  readonly languageOptions: AutocompleteOption[] = WORLD_LANGUAGES.map(l => ({ value: l, label: l }));

  getProfVariant(p: LanguageProficiency): 'info' | 'warning' | 'success' | 'primary' | 'secondary' {
    const map: Record<LanguageProficiency, 'info' | 'warning' | 'success' | 'primary' | 'secondary'> = {
      BEGINNER: 'info', INTERMEDIATE: 'warning', ADVANCED: 'success', FLUENT: 'primary', NATIVE: 'secondary',
    };
    return map[p] ?? 'info';
  }

  openAdd(): void {
    this.editingLang.set(null);
    this.langName.set('');
    this.langProficiency.set('');
    this.error.set(null);
    this.isModalOpen.set(true);
  }

  openEdit(lang: SpokenLanguage): void {
    this.editingLang.set(lang);
    this.langName.set(lang.language);
    this.langProficiency.set(lang.proficiency);
    this.error.set(null);
    this.isModalOpen.set(true);
  }

  close(): void { this.isModalOpen.set(false); }

  save(): void {
    if (!this.langName() || !this.langProficiency()) {
      this.error.set('Language and proficiency are required.');
      return;
    }
    this.saving.set(true);
    this.error.set(null);

    const payload: SpokenLanguage = {
      language: this.langName(),
      proficiency: this.langProficiency() as LanguageProficiency,
    };

    const editing = this.editingLang();
    const obs = editing
      ? this.profileService.updateLanguage(this.userId(), editing.id!, payload)
      : this.profileService.createLanguage(this.userId(), payload);

    obs.subscribe({
      next: () => { this.saving.set(false); this.isModalOpen.set(false); this.changed.emit(); },
      error: (err) => { this.saving.set(false); this.error.set(err?.error?.message || 'Failed to save language.'); },
    });
  }

  delete(lang: SpokenLanguage): void {
    this.langToDelete.set(lang);
    this.confirmDialogData.set({
      title: 'Remove Language',
      message: `Are you sure you want to remove "${lang.language}" from your languages?`,
      confirmText: 'Remove',
      cancelText: 'Cancel',
      variant: 'danger',
      icon: 'exclamation-triangle'
    });
    this.showConfirmDialog.set(true);
  }

  confirmDelete(): void {
    const lang = this.langToDelete();
    if (lang) {
      this.profileService.deleteLanguage(this.userId(), lang.id!).subscribe({
        next: () => {
          this.showConfirmDialog.set(false);
          this.langToDelete.set(null);
          this.changed.emit();
        },
        error: () => {
          this.showConfirmDialog.set(false);
          this.langToDelete.set(null);
        }
      });
    }
  }

  cancelDelete(): void {
    this.showConfirmDialog.set(false);
    this.langToDelete.set(null);
  }
}
