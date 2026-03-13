import { Component, inject, signal, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Store } from '@ngrx/store';
import { SlicePipe } from '@angular/common';
import {
  UiCard, UiInput, UiButton, UiAlert, UiBadge, UiSelect,
  IconComponent, Skeleton,
} from '../../../../shared';
import {
  OfferResponse,
  OfferContractType,
  OFFER_CONTRACT_TYPE_LABELS,
  OFFER_CONTRACT_TYPE_OPTIONS,
  getOfferStatusVariant,
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

@Component({
  selector: 'app-job-listings',
  standalone: true,
  imports: [
    UiCard, UiInput, UiButton, UiAlert, UiBadge, UiSelect,
    IconComponent, Skeleton, SlicePipe, RouterLink,
  ],
  templateUrl: './job-listings.html',
  styleUrl: './job-listings.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class JobListingsComponent implements OnInit {
  private readonly store = inject(Store);
  private readonly router = inject(Router);

  readonly offers = this.store.selectSignal(selectPublicOffers);
  readonly loading = this.store.selectSignal(selectPublicLoading);
  readonly totalElements = this.store.selectSignal(selectPublicTotalElements);
  readonly totalPages = this.store.selectSignal(selectPublicTotalPages);
  readonly currentPage = this.store.selectSignal(selectPublicCurrentPage);
  readonly error = this.store.selectSignal(selectOfferError);

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

  getTimeAgo(dateStr?: string): string {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));
    if (diffDays === 0) return 'Today';
    if (diffDays === 1) return 'Yesterday';
    if (diffDays < 7) return `${diffDays} days ago`;
    if (diffDays < 30) return `${Math.floor(diffDays / 7)} weeks ago`;
    return `${Math.floor(diffDays / 30)} months ago`;
  }
}



