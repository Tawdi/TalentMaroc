import { inject, Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { catchError, map, mergeMap, exhaustMap } from 'rxjs/operators';
import { SavedJobsService } from '../../../../core/services/saved-jobs.service';
import { SavedJobsActions } from './saved-jobs.actions';

@Injectable()
export class SavedJobsEffects {
  private readonly actions$ = inject(Actions);
  private readonly savedJobsService = inject(SavedJobsService);

  loadSavedJobs$ = createEffect(() =>
    this.actions$.pipe(
      ofType(SavedJobsActions.loadSavedJobs),
      exhaustMap(({ userId }) =>
        this.savedJobsService.getSavedJobs(userId).pipe(
          map((savedJobs) => SavedJobsActions.loadSavedJobsSuccess({ savedJobs })),
          catchError((error: Error) =>
            of(
              SavedJobsActions.loadSavedJobsFailure({
                error: error.message || 'Unable to load saved jobs.',
              })
            )
          )
        )
      )
    )
  );

  saveJob$ = createEffect(() =>
    this.actions$.pipe(
      ofType(SavedJobsActions.saveJob),
      mergeMap(({ userId, offer }) =>
        this.savedJobsService.saveJob(userId, offer.id).pipe(
          map((savedJob) =>
            SavedJobsActions.saveJobSuccess({
              savedJob: {
                ...savedJob,
                title: offer.title,
                companyName: offer.company?.companyName,
                location: offer.location,
                contractType: offer.contractType,
                offer,
              },
            })
          ),
          catchError((error: Error) =>
            of(
              SavedJobsActions.saveJobFailure({
                offerId: offer.id,
                error: error.message || 'Failed to save job.',
              })
            )
          )
        )
      )
    )
  );

  removeJob$ = createEffect(() =>
    this.actions$.pipe(
      ofType(SavedJobsActions.removeJob),
      mergeMap(({ userId, offerId }) =>
        this.savedJobsService.removeJob(userId, offerId).pipe(
          map(() => SavedJobsActions.removeJobSuccess({ offerId })),
          catchError((error: Error) =>
            of(
              SavedJobsActions.removeJobFailure({
                offerId,
                error: error.message || 'Failed to remove saved job.',
              })
            )
          )
        )
      )
    )
  );
}

