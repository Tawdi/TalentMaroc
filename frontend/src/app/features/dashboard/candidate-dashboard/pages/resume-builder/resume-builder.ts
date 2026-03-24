import { Component, ChangeDetectionStrategy, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { UiCard, UiButton, UiAlert, IconComponent, Skeleton } from '../../../../../shared';
import { AuthService, CandidateProfileService } from '../../../../../core';
import { CandidateProfile } from '../../../../../core/models/profile.model';

@Component({
  selector: 'app-resume-builder',
  standalone: true,
  imports: [UiCard, UiButton, UiAlert, IconComponent, Skeleton, RouterLink, DatePipe],
  templateUrl: './resume-builder.html',
  styleUrl: './resume-builder.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ResumeBuilderComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly profileService = inject(CandidateProfileService);

  readonly userId = computed(() => this.authService.currentUser()?.id ?? '');
  readonly profile = signal<CandidateProfile | null>(null);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  readonly profileNotFound = signal(false);
  readonly downloadError = signal<string | null>(null);

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    const uid = this.userId();
    if (!uid) return;

    this.loading.set(true);
    this.error.set(null);
    this.profileNotFound.set(false);

    this.profileService.getProfile(uid).subscribe({
      next: (profile) => {
        this.profile.set(profile);
        this.loading.set(false);
      },
      error: (err) => {
        this.loading.set(false);
        if (err.status === 404) {
          this.profileNotFound.set(true);
        } else {
          this.error.set('Unable to load your resume details.');
        }
      },
    });
  }

  downloadCv(): void {
    this.downloadError.set(null);
    this.profileService.downloadCv(this.userId()).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = this.profile()?.cvOriginalName || 'cv.pdf';
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: () => this.downloadError.set('Failed to download your CV.'),
    });
  }
}

