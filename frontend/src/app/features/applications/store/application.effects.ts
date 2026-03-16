import { Injectable, inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { map, exhaustMap, catchError } from 'rxjs/operators';
import { ApplicationService } from '../../../core/services/application.service';
import { ApplicationActions } from './application.actions';

@Injectable()
export class ApplicationEffects {
  private readonly actions$ = inject(Actions);
  private readonly applicationService = inject(ApplicationService);

  // ======================== CANDIDATE ========================

  apply$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ApplicationActions.apply),
      exhaustMap(({ userId, request }) =>
        this.applicationService.apply(userId, request).pipe(
          map((application) => ApplicationActions.applySuccess({ application })),
          catchError((err) =>
            of(ApplicationActions.applyFailure({
              error: err?.error?.message || 'Failed to submit application',
            })),
          ),
        ),
      ),
    ),
  );

  loadMyApplications$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ApplicationActions.loadMyApplications),
      exhaustMap(({ userId, page, size }) =>
        this.applicationService.getMyApplications(userId, page ?? 0, size ?? 20).pipe(
          map((applicationsPage) =>
            ApplicationActions.loadMyApplicationsSuccess({ applicationsPage }),
          ),
          catchError((err) =>
            of(ApplicationActions.loadMyApplicationsFailure({
              error: err?.error?.message || 'Failed to load applications',
            })),
          ),
        ),
      ),
    ),
  );

  withdrawApplication$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ApplicationActions.withdrawApplication),
      exhaustMap(({ userId, applicationId }) =>
        this.applicationService.withdrawApplication(userId, applicationId).pipe(
          map((application) =>
            ApplicationActions.withdrawApplicationSuccess({ application }),
          ),
          catchError((err) =>
            of(ApplicationActions.withdrawApplicationFailure({
              error: err?.error?.message || 'Failed to withdraw application',
            })),
          ),
        ),
      ),
    ),
  );

  // ======================== COMPANY ========================

  loadOfferApplications$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ApplicationActions.loadOfferApplications),
      exhaustMap(({ userId, offerId, status, page, size }) =>
        this.applicationService
          .getApplicationsForOffer(userId, offerId, status, page ?? 0, size ?? 20)
          .pipe(
            map((applicationsPage) =>
              ApplicationActions.loadOfferApplicationsSuccess({ applicationsPage }),
            ),
            catchError((err) =>
              of(ApplicationActions.loadOfferApplicationsFailure({
                error: err?.error?.message || 'Failed to load applications',
              })),
            ),
          ),
      ),
    ),
  );

  updateApplicationStatus$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ApplicationActions.updateApplicationStatus),
      exhaustMap(({ userId, applicationId, request }) =>
        this.applicationService.updateApplicationStatus(userId, applicationId, request).pipe(
          map((application) =>
            ApplicationActions.updateApplicationStatusSuccess({ application }),
          ),
          catchError((err) =>
            of(ApplicationActions.updateApplicationStatusFailure({
              error: err?.error?.message || 'Failed to update application status',
            })),
          ),
        ),
      ),
    ),
  );
}
