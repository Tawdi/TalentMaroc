import { Component, input, output, signal, inject, ChangeDetectionStrategy } from '@angular/core';
import { UiCard, UiButton, UiInput, UiTextarea, UiAlert, UiDateInput, IconComponent } from '../../../../shared';
import { CandidateProfileService } from '../../../../core';
import { CandidateProfile, CreateProfileRequest } from '../../../../core/models/profile.model';

@Component({
  selector: 'app-profile-create',
  standalone: true,
  imports: [UiCard, UiButton, UiInput, UiTextarea, UiAlert, UiDateInput, IconComponent],
  templateUrl: './profile-create.html',
  styleUrl: './profile-create.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProfileCreateComponent {
  private readonly profileService = inject(CandidateProfileService);

  userId = input.required<string>();
  created = output<CandidateProfile>();

  // Form state
  firstName = signal('');
  lastName = signal('');
  headline = signal('');
  about = signal('');
  phone = signal('');
  dateOfBirth = signal('');
  city = signal('');
  country = signal('');
  street = signal('');

  saving = signal(false);
  error = signal<string | null>(null);

  submit(): void {
    if (!this.firstName() || !this.lastName()) {
      this.error.set('First name and last name are required.');
      return;
    }

    this.saving.set(true);
    this.error.set(null);

    const request: CreateProfileRequest = {
      userId: this.userId(),
      firstName: this.firstName(),
      lastName: this.lastName(),
      headline: this.headline() || undefined,
      about: this.about() || undefined,
      phone: this.phone() || undefined,
      dateOfBirth: this.dateOfBirth() || undefined,
      address: (this.city() || this.country()) ? {
        street: this.street() || undefined,
        city: this.city(),
        country: this.country(),
      } : undefined,
    };

    this.profileService.createProfile(request).subscribe({
      next: (profile) => {
        this.saving.set(false);
        this.created.emit(profile);
      },
      error: (err) => {
        this.saving.set(false);
        this.error.set(err?.error?.message || 'Failed to create profile.');
      },
    });
  }
}

