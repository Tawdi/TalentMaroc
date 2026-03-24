import { Component, ChangeDetectionStrategy, OnInit, inject, computed } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { Store } from '@ngrx/store';
import {
  UiCard, UiButton, UiBadge, UiAlert, IconComponent, Skeleton,
} from '../../../shared';
import { AuthService } from '../../../core/services/auth.service';
import {
  COMPANY_STATUS_LABELS,
  getCompanyStatusVariant,
  OfferResponse,
} from '../../../core/models/company-offers.model';
import {
  CompanyActions,
  OfferActions,
  selectCompany,
  selectCompanyLoading,
  selectCompanyError,
  selectCompanyNotFound,
  selectMyOffers,
  selectMyOffersLoading,
  selectMyOffersTotalElements,
  selectOfferError,
} from '../../company-offers/store';

@Component({
  selector: 'app-company-dashboard',
  standalone: true,
  imports: [
    UiCard, UiButton, UiBadge, UiAlert, IconComponent, Skeleton,
    RouterLink, DatePipe,
  ],
  templateUrl: './company-dashboard.html',
  styleUrl: './company-dashboard.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CompanyDashboardComponent implements OnInit {
  private readonly store = inject(Store);
  private readonly authService = inject(AuthService);

  readonly userId = computed(() => this.authService.currentUser()?.id ?? '');
  readonly userName = computed(() => this.authService.currentUser()?.name
    || this.authService.currentUser()?.username
    || 'Company');

  readonly company = this.store.selectSignal(selectCompany);
  readonly companyLoading = this.store.selectSignal(selectCompanyLoading);
  readonly companyNotFound = this.store.selectSignal(selectCompanyNotFound);
  readonly companyError = this.store.selectSignal(selectCompanyError);

  readonly offers = this.store.selectSignal(selectMyOffers);
  readonly offersLoading = this.store.selectSignal(selectMyOffersLoading);
  readonly offersTotal = this.store.selectSignal(selectMyOffersTotalElements);
  readonly offersError = this.store.selectSignal(selectOfferError);

  readonly statusLabels = COMPANY_STATUS_LABELS;
  readonly getStatusVariant = getCompanyStatusVariant;

  readonly recentOffers = computed(() => this.offers().slice(0, 3));

  ngOnInit(): void {
    this.loadCompany();
    this.loadOffers();
  }

  loadCompany(): void {
    const uid = this.userId();
    if (!uid) return;
    this.store.dispatch(CompanyActions.loadCompany({ userId: uid }));
  }

  loadOffers(): void {
    const uid = this.userId();
    if (!uid) return;
    this.store.dispatch(OfferActions.loadMyOffers({ userId: uid, page: 0 }));
  }

  getOfferTitle(offer: OfferResponse): string {
    return offer.title || `Offer #${offer.id}`;
  }
}

