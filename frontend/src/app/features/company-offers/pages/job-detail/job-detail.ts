import { Component, inject, OnInit, OnDestroy, ChangeDetectionStrategy, signal, computed, effect } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { DatePipe } from '@angular/common';
import {
  UiCard, UiButton, UiAlert, UiBadge, UiModal, UiTextarea, IconComponent, Skeleton,
} from '../../../../shared';
import {
  OFFER_CONTRACT_TYPE_LABELS,
  OFFER_STATUS_LABELS,
  getOfferStatusVariant,
} from '../../../../core/models/company-offers.model';
import { CandidateProfile } from '../../../../core/models/profile.model';
import { AuthService } from '../../../../core/services/auth.service';
import { CandidateProfileService } from '../../../../core/services/candidate-profile.service';
import { environment } from '../../../../../environments/environment';
import {
  OfferActions,
  selectSelectedOffer,
  selectSelectedLoading,
  selectOfferError,
} from '../../store';
import {
  ApplicationActions,
  selectApplying,
  selectApplySuccess,
  selectApplicationError,
  selectAppliedStatusMap,
  selectCheckingApplied,
} from '../../../applications/store';

@Component({
  selector: 'app-job-detail',
  standalone: true,
  imports: [UiCard, UiButton, UiAlert, UiBadge, UiModal, UiTextarea, IconComponent, Skeleton, DatePipe],
  templateUrl: './job-detail.html',
  styleUrl: './job-detail.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class JobDetailComponent implements OnInit, OnDestroy {
  private readonly store = inject(Store);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);
  private readonly profileService = inject(CandidateProfileService);

  readonly offer = this.store.selectSignal(selectSelectedOffer);
  readonly loading = this.store.selectSignal(selectSelectedLoading);
  readonly error = this.store.selectSignal(selectOfferError);

  readonly applying = this.store.selectSignal(selectApplying);
  readonly applySuccess = this.store.selectSignal(selectApplySuccess);
  readonly applyError = this.store.selectSignal(selectApplicationError);
  readonly appliedStatusMap = this.store.selectSignal(selectAppliedStatusMap);
  readonly checkingApplied = this.store.selectSignal(selectCheckingApplied);

  readonly contractLabels = OFFER_CONTRACT_TYPE_LABELS;
  readonly statusLabels = OFFER_STATUS_LABELS;
  readonly getStatusVariant = getOfferStatusVariant;

  readonly isLoggedIn = computed(() => !!this.authService.currentUser());
  readonly userId = computed(() => this.authService.currentUser()?.id ?? '');
  readonly currentRole = computed(() => this.authService.currentUser()?.role ?? 'GUEST');
  readonly isGuest = computed(() => !this.isLoggedIn());
  readonly isCandidateRole = computed(() => this.currentRole() === 'CANDIDATE');
  readonly canSeeApplyCta = computed(() => this.isGuest() || this.isCandidateRole());
  readonly hasApplied = computed(() => {
    const offerId = this.offer()?.id;
    if (!offerId) return false;
    return !!this.appliedStatusMap()[offerId];
  });
  readonly shouldShowAlreadyApplied = computed(() => this.isCandidateRole() && this.hasApplied());

  // Apply modal state
  isApplyModalOpen = signal(false);
  coverLetter = signal('');

  // CV state
  profileLoading = signal(false);
  candidateProfile = signal<CandidateProfile | null>(null);
  cvUploading = signal(false);
  cvError = signal<string | null>(null);

  /** Whether the candidate has a CV on their profile */
  readonly hasCv = computed(() => !!this.candidateProfile()?.cvOriginalName);
  readonly cvFileName = computed(() => this.candidateProfile()?.cvOriginalName ?? '');

  private readonly checkAppliedEffect = effect(() => {
    const offerId = this.offer()?.id;
    const userId = this.userId();
    if (!offerId || !userId || !this.isCandidateRole()) {
      return;
    }
    this.store.dispatch(ApplicationActions.checkApplied({ userId, offerId }));
  });

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('offerId'));
    if (id) {
      this.store.dispatch(OfferActions.loadOfferById({ offerId: id }));
    }
  }

  ngOnDestroy(): void {
    this.store.dispatch(OfferActions.clearSelectedOffer());
    this.store.dispatch(ApplicationActions.clearApplySuccess());
  }

  goBack(): void {
    this.router.navigate(['/jobs']);
  }

  // ======================== Apply Flow ========================

  openApplyModal(): void {
    if (!this.canSeeApplyCta()) {
      return;
    }
    if (!this.isLoggedIn()) {
      this.router.navigate(['/auth/login']);
      return;
    }
    if (this.shouldShowAlreadyApplied()) {
      return;
    }
    this.coverLetter.set('');
    this.cvError.set(null);
    this.store.dispatch(ApplicationActions.clearError());
    this.store.dispatch(ApplicationActions.clearApplySuccess());
    this.isApplyModalOpen.set(true);

    // Fetch the candidate's profile to check for existing CV
    this.fetchProfile();
  }

  closeApplyModal(): void {
    this.isApplyModalOpen.set(false);
  }

  submitApplication(): void {
    if (!this.isCandidateRole()) {
      return;
    }
    // Must have a CV
    if (!this.hasCv()) {
      this.cvError.set('Please upload your CV before applying.');
      return;
    }

    const offerId = this.offer()?.id;
    if (!offerId) return;

    // Build the CV URL from the candidate profile service endpoint
    const cvUrl = `${environment.apiUrl}/candidate-profile/api/v1/profiles/user/${this.userId()}/cv`;

    this.store.dispatch(
      ApplicationActions.apply({
        userId: this.userId(),
        request: {
          offerId,
          cvUrl,
          coverLetter: this.coverLetter().trim() || undefined,
        },
      }),
    );
  }

  /** Navigate to profile page to manage CV */
  goToProfile(): void {
    this.closeApplyModal();
    this.router.navigate(['/dashboard/profile']);
  }

  // ======================== CV Upload (inline) ========================

  onCvFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    // Validate file type
    if (file.type !== 'application/pdf') {
      this.cvError.set('Only PDF files are accepted.');
      input.value = '';
      return;
    }

    // Validate file size (10MB)
    if (file.size > 10 * 1024 * 1024) {
      this.cvError.set('File size must be less than 10MB.');
      input.value = '';
      return;
    }

    this.cvUploading.set(true);
    this.cvError.set(null);

    this.profileService.uploadCv(this.userId(), file).subscribe({
      next: (profile) => {
        this.candidateProfile.set(profile);
        this.cvUploading.set(false);
        input.value = '';
      },
      error: (err) => {
        this.cvError.set(err?.error?.message || 'Failed to upload CV. Please try again.');
        this.cvUploading.set(false);
        input.value = '';
      },
    });
  }

  // ======================== Private ========================

  private fetchProfile(): void {
    const uid = this.userId();
    if (!uid) return;

    this.profileLoading.set(true);
    this.candidateProfile.set(null);

    this.profileService.getProfile(uid).subscribe({
      next: (profile) => {
        this.candidateProfile.set(profile);
        this.profileLoading.set(false);
      },
      error: () => {
        // Profile may not exist yet — that's fine, user can still upload a CV
        this.profileLoading.set(false);
      },
    });
  }
}
