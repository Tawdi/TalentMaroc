import { Component, ChangeDetectionStrategy, OnInit, inject, computed, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { Store } from '@ngrx/store';
import {
  UiCard, UiButton, UiBadge, UiAlert, IconComponent, Skeleton,
} from '../../../shared';
import { AuthService, CandidateProfileService } from '../../../core';
import { CandidateProfile } from '../../../core/models/profile.model';
import {
  APPLICATION_STATUS_LABELS,
  getApplicationStatusVariant,
} from '../../../core/models/application.model';
import {
  OFFER_CONTRACT_TYPE_LABELS,
} from '../../../core/models/company-offers.model';
import {
  ApplicationActions,
  selectMyApplications,
  selectMyLoading,
  selectMyTotalElements,
  selectApplicationError,
} from '../../applications/store';
import {
  OfferActions,
  selectPublicOffers,
  selectPublicLoading,
  selectOfferError,
} from '../../company-offers/store';
import { SavedJobsActions, selectSavedJobsTotal } from './store';

@Component({
  selector: 'app-candidate-dashboard',
  standalone: true,
  imports: [
    UiCard, UiButton, UiBadge, UiAlert, IconComponent, Skeleton,
    RouterLink, DatePipe,
  ],
  templateUrl: './candidate-dashboard.html',
  styleUrl: './candidate-dashboard.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CandidateDashboardComponent implements OnInit {
  private readonly store = inject(Store);
  private readonly authService = inject(AuthService);
  private readonly profileService = inject(CandidateProfileService);

  readonly userId = computed(() => this.authService.currentUser()?.id ?? '');
  readonly userName = computed(() => this.authService.currentUser()?.name
    || this.authService.currentUser()?.username
    || 'Candidate');

  readonly applications = this.store.selectSignal(selectMyApplications);
  readonly applicationsLoading = this.store.selectSignal(selectMyLoading);
  readonly applicationsTotal = this.store.selectSignal(selectMyTotalElements);
  readonly applicationsError = this.store.selectSignal(selectApplicationError);

  readonly offers = this.store.selectSignal(selectPublicOffers);
  readonly offersLoading = this.store.selectSignal(selectPublicLoading);
  readonly offersError = this.store.selectSignal(selectOfferError);

  readonly profile = signal<CandidateProfile | null>(null);
  readonly profileLoading = signal(false);
  readonly profileError = signal<string | null>(null);
  readonly profileNotFound = signal(false);

  readonly savedJobsTotal = this.store.selectSignal(selectSavedJobsTotal);

  readonly statusLabels = APPLICATION_STATUS_LABELS;
  readonly getStatusVariant = getApplicationStatusVariant;
  readonly contractLabels: Record<string, string> = OFFER_CONTRACT_TYPE_LABELS as Record<string, string>;

  readonly recentApplications = computed(() => this.applications().slice(0, 3));
  readonly recommendedOffers = computed(() => this.offers().slice(0, 4));

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

  ngOnInit(): void {
    this.loadApplications();
    this.loadRecommendedOffers();
    this.loadProfile();
    this.loadSavedJobs();
  }

  loadApplications(): void {
    const uid = this.userId();
    if (!uid) return;
    this.store.dispatch(ApplicationActions.loadMyApplications({ userId: uid, page: 0 }));
  }

  loadRecommendedOffers(): void {
    this.store.dispatch(OfferActions.loadActiveOffers({ page: 0, size: 6 }));
  }

  loadProfile(): void {
    const uid = this.userId();
    if (!uid) return;

    this.profileLoading.set(true);
    this.profileError.set(null);
    this.profileNotFound.set(false);

    this.profileService.getProfile(uid).subscribe({
      next: (p) => {
        this.profile.set(p);
        this.profileLoading.set(false);
      },
      error: (err) => {
        this.profileLoading.set(false);
        if (err.status === 404) {
          this.profileNotFound.set(true);
        } else {
          this.profileError.set('Failed to load profile details.');
        }
      },
    });
  }

  loadSavedJobs(): void {
    const uid = this.userId();
    if (!uid) return;
    this.store.dispatch(SavedJobsActions.loadSavedJobs({ userId: uid }));
  }
}
