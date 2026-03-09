import { Component, inject, signal, computed, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { UiCard, UiButton, UiAlert, Skeleton, IconComponent } from '../../../shared';
import { AuthService, CandidateProfileService } from '../../../core';
import {
  CandidateProfile,
} from '../../../core/models/profile.model';
import { ProfileInfoSectionComponent } from './sections/profile-info-section';
import { SkillsSectionComponent } from './sections/skills-section';
import { LanguagesSectionComponent } from './sections/languages-section';
import { ExperiencesSectionComponent } from './sections/experiences-section';
import { FormationsSectionComponent } from './sections/formations-section';
import { ProjectsSectionComponent } from './sections/projects-section';
import { CvSectionComponent } from './sections/cv-section';
import { ProfileCreateComponent } from './sections/profile-create';

export type ProfileTab = 'overview' | 'experience' | 'education' | 'skills' | 'projects';

@Component({
  selector: 'app-candidate-profile',
  standalone: true,
  imports: [
    UiCard, UiButton, UiAlert, Skeleton, IconComponent,
    ProfileInfoSectionComponent, SkillsSectionComponent, LanguagesSectionComponent,
    ExperiencesSectionComponent, FormationsSectionComponent, ProjectsSectionComponent,
    CvSectionComponent, ProfileCreateComponent,
  ],
  templateUrl: './candidate-profile.html',
  styleUrl: './candidate-profile.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CandidateProfileComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly profileService = inject(CandidateProfileService);

  // State
  readonly profile = signal<CandidateProfile | null>(null);
  readonly isLoading = signal(true);
  readonly error = signal<string | null>(null);
  readonly activeTab = signal<ProfileTab>('overview');
  readonly profileNotFound = signal(false);

  // Computed
  readonly userId = computed(() => this.authService.currentUser()?.id ?? '');
  readonly fullName = computed(() => {
    const p = this.profile();
    return p ? `${p.firstName} ${p.lastName}` : '';
  });
  readonly initials = computed(() => {
    const p = this.profile();
    if (!p) return '';
    return (p.firstName[0] + p.lastName[0]).toUpperCase();
  });
  readonly completionPercentage = computed(() => {
    const p = this.profile();
    if (!p) return 0;
    let score = 0;
    const checks = [
      p.firstName, p.lastName, p.headline, p.about, p.phone,
      p.dateOfBirth, p.address?.city, p.cvOriginalName,
      (p.skills?.length ?? 0) > 0, (p.experiences?.length ?? 0) > 0,
      (p.formations?.length ?? 0) > 0, (p.spokenLanguages?.length ?? 0) > 0,
    ];
    checks.forEach(c => { if (c) score++; });
    return Math.round((score / checks.length) * 100);
  });

  readonly tabs: { key: ProfileTab; label: string; icon: string }[] = [
    { key: 'overview', label: 'Overview', icon: 'user' },
    { key: 'experience', label: 'Experience', icon: 'briefcase' },
    { key: 'education', label: 'Education', icon: 'academic-cap' },
    { key: 'skills', label: 'Skills & Languages', icon: 'wrench' },
    { key: 'projects', label: 'Projects', icon: 'rocket' },
  ];

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    const uid = this.userId();
    if (!uid) return;

    this.isLoading.set(true);
    this.error.set(null);
    this.profileNotFound.set(false);

    this.profileService.getProfile(uid).subscribe({
      next: (p) => {
        this.profile.set(p);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.isLoading.set(false);
        if (err.status === 404) {
          this.profileNotFound.set(true);
        } else {
          this.error.set('Failed to load profile. Please try again.');
        }
      },
    });
  }

  setTab(tab: ProfileTab): void {
    this.activeTab.set(tab);
  }

  onProfileCreated(p: CandidateProfile): void {
    this.profile.set(p);
    this.profileNotFound.set(false);
  }

  onProfileUpdated(p: CandidateProfile): void {
    this.profile.set(p);
  }

  /** Called by child sections after they mutate a sub-resource */
  refreshProfile(): void {
    const uid = this.userId();
    if (!uid) return;
    this.profileService.getProfile(uid).subscribe({
      next: (p) => this.profile.set(p),
    });
  }
}

