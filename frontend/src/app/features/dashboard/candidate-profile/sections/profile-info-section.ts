import { Component, input, output, signal, inject, ChangeDetectionStrategy } from '@angular/core';
import { UiCard, UiButton, UiInput, UiTextarea, UiModal, UiAlert, UiDateInput, IconComponent } from '../../../../shared';
import { CandidateProfileService } from '../../../../core';
import { CandidateProfile, UpdateProfileRequest } from '../../../../core/models/profile.model';

@Component({
  selector: 'app-profile-info-section',
  standalone: true,
  imports: [UiCard, UiButton, UiInput, UiTextarea, UiModal, UiAlert, UiDateInput, IconComponent],
  templateUrl: './profile-info-section.html',
  styleUrl: './profile-info-section.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProfileInfoSectionComponent {
  private readonly profileService = inject(CandidateProfileService);

  profile = input.required<CandidateProfile>();
  userId = input.required<string>();
  updated = output<CandidateProfile>();

  isEditing = signal(false);
  saving = signal(false);
  error = signal<string | null>(null);

  // Form signals
  firstName = signal('');
  lastName = signal('');
  headline = signal('');
  about = signal('');
  phone = signal('');
  dateOfBirth = signal('');
  street = signal('');
  city = signal('');
  state = signal('');
  zipCode = signal('');
  country = signal('');

  openEdit(): void {
    const p = this.profile();
    this.firstName.set(p.firstName);
    this.lastName.set(p.lastName);
    this.headline.set(p.headline ?? '');
    this.about.set(p.about ?? '');
    this.phone.set(p.phone ?? '');
    this.dateOfBirth.set(p.dateOfBirth ?? '');
    this.street.set(p.address?.street ?? '');
    this.city.set(p.address?.city ?? '');
    this.state.set(p.address?.state ?? '');
    this.zipCode.set(p.address?.zipCode ?? '');
    this.country.set(p.address?.country ?? '');
    this.error.set(null);
    this.isEditing.set(true);
  }

  closeEdit(): void {
    this.isEditing.set(false);
  }

  save(): void {
    this.saving.set(true);
    this.error.set(null);

    const req: UpdateProfileRequest = {
      firstName: this.firstName(),
      lastName: this.lastName(),
      headline: this.headline() || undefined,
      about: this.about() || undefined,
      phone: this.phone() || undefined,
      dateOfBirth: this.dateOfBirth() || undefined,
      address: {
        street: this.street() || undefined,
        city: this.city(),
        state: this.state() || undefined,
        zipCode: this.zipCode() || undefined,
        country: this.country(),
      },
    };

    this.profileService.updateProfile(this.userId(), req).subscribe({
      next: (p) => {
        this.saving.set(false);
        this.isEditing.set(false);
        this.updated.emit(p);
      },
      error: (err) => {
        this.saving.set(false);
        this.error.set(err?.error?.message || 'Failed to update profile.');
      },
    });
  }
}

