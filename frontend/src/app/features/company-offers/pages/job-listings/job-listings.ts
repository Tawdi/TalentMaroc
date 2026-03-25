import { Component, inject, signal, OnInit, ChangeDetectionStrategy, computed } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Store } from '@ngrx/store';
import {
  UiCard, UiInput, UiButton, UiAlert, UiBadge, UiSelect,
  IconComponent, Skeleton,
} from '../../../../shared';
import {
  OfferResponse,
  OfferContractType,
  OFFER_CONTRACT_TYPE_LABELS,
  OFFER_CONTRACT_TYPE_OPTIONS,
} from '../../../../core/models/company-offers.model';
import {
  OfferActions,
  selectPublicOffers,
  selectPublicLoading,
  selectPublicTotalElements,
  selectPublicTotalPages,
  selectPublicCurrentPage,
  selectOfferError,
} from '../../store';
import { JobOfferCardComponent } from '../../components/job-offer-card/job-offer-card';
import { AuthService } from '../../../../core/services/auth.service';
import {
  SavedJobsActions,
  selectSavedJobsEntities,
  selectSavedJobsPendingOfferIds,
} from '../../../dashboard/candidate-dashboard/store';

@Component({
  selector: 'app-job-listings',
  standalone: true,
  imports: [
    UiCard, UiInput, UiButton, UiAlert, UiBadge, UiSelect,
    IconComponent, Skeleton, RouterLink, JobOfferCardComponent,
  ],
  templateUrl: './job-listings.html',
  styleUrl: './job-listings.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class JobListingsComponent implements OnInit {
  private readonly store = inject(Store);
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);

  readonly offers = this.store.selectSignal(selectPublicOffers);
  readonly loading = this.store.selectSignal(selectPublicLoading);
  readonly totalElements = this.store.selectSignal(selectPublicTotalElements);
  readonly totalPages = this.store.selectSignal(selectPublicTotalPages);
  readonly currentPage = this.store.selectSignal(selectPublicCurrentPage);
  readonly error = this.store.selectSignal(selectOfferError);
  readonly savedJobsEntities = this.store.selectSignal(selectSavedJobsEntities);
  readonly pendingOfferIds = this.store.selectSignal(selectSavedJobsPendingOfferIds);
  readonly canSaveJobs = computed(() => this.authService.currentUser()?.role === 'CANDIDATE');
  readonly isAuthenticated =   this.authService.isAuthenticated;

  readonly contractLabels = OFFER_CONTRACT_TYPE_LABELS;
  readonly contractOptions = [
    { value: '', label: 'All Types' },
    ...OFFER_CONTRACT_TYPE_OPTIONS.map(o => ({ value: o.value, label: o.label })),
  ];

  // Filters
  keyword = signal('');
  location = signal('');
  contractType = signal('');

  ngOnInit(): void {
    this.search();
    this.loadSavedJobs();
  }

  search(page = 0): void {
    const keyword = this.keyword().trim();
    const location = this.location().trim();
    const ct = this.contractType();

    const hasFilters = keyword || location || ct;

    if (hasFilters) {
      const filters: { keyword?: string; location?: string; contractType?: OfferContractType } = {};
      if (keyword) filters.keyword = keyword;
      if (location) filters.location = location;
      if (ct) filters.contractType = ct as OfferContractType;
      this.store.dispatch(OfferActions.filterOffers({ filters, page }));
    } else {
      this.store.dispatch(OfferActions.loadActiveOffers({ page }));
    }
  }

  clearFilters(): void {
    this.keyword.set('');
    this.location.set('');
    this.contractType.set('');
    this.search();
  }

  viewOffer(offer: OfferResponse): void {
    this.router.navigate(['/jobs', offer.id]);
  }

  goToPage(page: number): void {
    this.search(page);
  }

  get pages(): number[] {
    const total = this.totalPages();
    return Array.from({ length: total }, (_, i) => i);
  }

  isOfferSaved(offerId: number): boolean {
    return !!this.savedJobsEntities()[offerId];
  }

  isOfferPending(offerId: number): boolean {
    return this.pendingOfferIds().includes(offerId);
  }

  toggleSave(offer: OfferResponse): void {
    const user = this.authService.currentUser();
    if (!user) {
      this.router.navigate(['/auth/login'], { queryParams: { returnUrl: this.router.url } });
      return;
    }
    if (user.role !== 'CANDIDATE') {
      return;
    }

    if (this.isOfferSaved(offer.id)) {
      this.store.dispatch(SavedJobsActions.removeJob({ userId: user.id, offerId: offer.id }));
    } else {
      this.store.dispatch(SavedJobsActions.saveJob({ userId: user.id, offer }));
    }
  }

  private loadSavedJobs(): void {
    const user = this.authService.currentUser();
    if (!user || user.role !== 'CANDIDATE') return;
    this.store.dispatch(SavedJobsActions.loadSavedJobs({ userId: user.id }));
  }
}
