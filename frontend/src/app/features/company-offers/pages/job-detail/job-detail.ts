import { Component, inject, OnInit, OnDestroy, ChangeDetectionStrategy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { DatePipe } from '@angular/common';
import {
  UiCard, UiButton, UiAlert, UiBadge, IconComponent, Skeleton,
} from '../../../../shared';
import {
  OFFER_CONTRACT_TYPE_LABELS,
  OFFER_STATUS_LABELS,
  getOfferStatusVariant,
} from '../../../../core/models/company-offers.model';
import {
  OfferActions,
  selectSelectedOffer,
  selectSelectedLoading,
  selectOfferError,
} from '../../store';

@Component({
  selector: 'app-job-detail',
  standalone: true,
  imports: [UiCard, UiButton, UiAlert, UiBadge, IconComponent, Skeleton, DatePipe],
  templateUrl: './job-detail.html',
  styleUrl: './job-detail.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class JobDetailComponent implements OnInit, OnDestroy {
  private readonly store = inject(Store);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly offer = this.store.selectSignal(selectSelectedOffer);
  readonly loading = this.store.selectSignal(selectSelectedLoading);
  readonly error = this.store.selectSignal(selectOfferError);

  readonly contractLabels = OFFER_CONTRACT_TYPE_LABELS;
  readonly statusLabels = OFFER_STATUS_LABELS;
  readonly getStatusVariant = getOfferStatusVariant;

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('offerId'));
    if (id) {
      this.store.dispatch(OfferActions.loadOfferById({ offerId: id }));
    }
  }

  ngOnDestroy(): void {
    this.store.dispatch(OfferActions.clearSelectedOffer());
  }

  goBack(): void {
    this.router.navigate(['/jobs']);
  }
}

