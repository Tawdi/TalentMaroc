import { Injectable, inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { map, exhaustMap, catchError } from 'rxjs/operators';
import { OfferService } from '../../../core/services/offer.service';
import { OfferActions } from './offer.actions';

@Injectable()
export class OfferEffects {
  private readonly actions$ = inject(Actions);
  private readonly offerService = inject(OfferService);

  loadMyOffers$ = createEffect(() =>
    this.actions$.pipe(
      ofType(OfferActions.loadMyOffers),
      exhaustMap(({ userId, page, size }) =>
        this.offerService.getMyOffers(userId, page ?? 0, size ?? 20).pipe(
          map((offersPage) => OfferActions.loadMyOffersSuccess({ offersPage })),
          catchError((err) =>
            of(OfferActions.loadMyOffersFailure({
              error: err?.error?.message || 'Failed to load offers',
            })),
          ),
        ),
      ),
    ),
  );

  createOffer$ = createEffect(() =>
    this.actions$.pipe(
      ofType(OfferActions.createOffer),
      exhaustMap(({ userId, request }) =>
        this.offerService.createOffer(userId, request).pipe(
          map((offer) => OfferActions.createOfferSuccess({ offer })),
          catchError((err) =>
            of(OfferActions.createOfferFailure({
              error: err?.error?.message || 'Failed to create offer',
            })),
          ),
        ),
      ),
    ),
  );

  updateOffer$ = createEffect(() =>
    this.actions$.pipe(
      ofType(OfferActions.updateOffer),
      exhaustMap(({ userId, offerId, request }) =>
        this.offerService.updateOffer(userId, offerId, request).pipe(
          map((offer) => OfferActions.updateOfferSuccess({ offer })),
          catchError((err) =>
            of(OfferActions.updateOfferFailure({
              error: err?.error?.message || 'Failed to update offer',
            })),
          ),
        ),
      ),
    ),
  );

  deleteOffer$ = createEffect(() =>
    this.actions$.pipe(
      ofType(OfferActions.deleteOffer),
      exhaustMap(({ userId, offerId }) =>
        this.offerService.deleteOffer(userId, offerId).pipe(
          map(() => OfferActions.deleteOfferSuccess({ offerId })),
          catchError((err) =>
            of(OfferActions.deleteOfferFailure({
              error: err?.error?.message || 'Failed to delete offer',
            })),
          ),
        ),
      ),
    ),
  );

  publishOffer$ = createEffect(() =>
    this.actions$.pipe(
      ofType(OfferActions.publishOffer),
      exhaustMap(({ userId, offerId }) =>
        this.offerService.publishOffer(userId, offerId).pipe(
          map((offer) => OfferActions.statusChangeSuccess({ offer })),
          catchError((err) =>
            of(OfferActions.statusChangeFailure({
              error: err?.error?.message || 'Failed to publish offer',
            })),
          ),
        ),
      ),
    ),
  );

  closeOffer$ = createEffect(() =>
    this.actions$.pipe(
      ofType(OfferActions.closeOffer),
      exhaustMap(({ userId, offerId }) =>
        this.offerService.closeOffer(userId, offerId).pipe(
          map((offer) => OfferActions.statusChangeSuccess({ offer })),
          catchError((err) =>
            of(OfferActions.statusChangeFailure({
              error: err?.error?.message || 'Failed to close offer',
            })),
          ),
        ),
      ),
    ),
  );

  archiveOffer$ = createEffect(() =>
    this.actions$.pipe(
      ofType(OfferActions.archiveOffer),
      exhaustMap(({ userId, offerId }) =>
        this.offerService.archiveOffer(userId, offerId).pipe(
          map((offer) => OfferActions.statusChangeSuccess({ offer })),
          catchError((err) =>
            of(OfferActions.statusChangeFailure({
              error: err?.error?.message || 'Failed to archive offer',
            })),
          ),
        ),
      ),
    ),
  );

  loadActiveOffers$ = createEffect(() =>
    this.actions$.pipe(
      ofType(OfferActions.loadActiveOffers),
      exhaustMap(({ page, size }) =>
        this.offerService.getActiveOffers(page ?? 0, size ?? 20).pipe(
          map((offersPage) => OfferActions.loadActiveOffersSuccess({ offersPage })),
          catchError((err) =>
            of(OfferActions.loadActiveOffersFailure({
              error: err?.error?.message || 'Failed to load offers',
            })),
          ),
        ),
      ),
    ),
  );

  loadOfferById$ = createEffect(() =>
    this.actions$.pipe(
      ofType(OfferActions.loadOfferById),
      exhaustMap(({ offerId }) =>
        this.offerService.getOfferById(offerId).pipe(
          map((offer) => OfferActions.loadOfferByIdSuccess({ offer })),
          catchError((err) =>
            of(OfferActions.loadOfferByIdFailure({
              error: err?.error?.message || 'Failed to load offer',
            })),
          ),
        ),
      ),
    ),
  );

  filterOffers$ = createEffect(() =>
    this.actions$.pipe(
      ofType(OfferActions.filterOffers),
      exhaustMap(({ filters, page, size }) =>
        this.offerService.filterOffers(filters, page ?? 0, size ?? 20).pipe(
          map((offersPage) => OfferActions.filterOffersSuccess({ offersPage })),
          catchError((err) =>
            of(OfferActions.filterOffersFailure({
              error: err?.error?.message || 'Failed to filter offers',
            })),
          ),
        ),
      ),
    ),
  );
}

