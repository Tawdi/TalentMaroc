import { createFeatureSelector, createSelector } from '@ngrx/store';
import { savedJobsAdapter, SavedJobsState, SAVED_JOBS_FEATURE_KEY } from './saved-jobs.reducer';

const { selectAll, selectEntities, selectTotal } = savedJobsAdapter.getSelectors();

export const selectSavedJobsState = createFeatureSelector<SavedJobsState>(SAVED_JOBS_FEATURE_KEY);

export const selectSavedJobs = createSelector(selectSavedJobsState, selectAll);
export const selectSavedJobsEntities = createSelector(selectSavedJobsState, selectEntities);
export const selectSavedJobsTotal = createSelector(selectSavedJobsState, selectTotal);
export const selectSavedJobsLoading = createSelector(selectSavedJobsState, (state) => state.loading);
export const selectSavedJobsLoaded = createSelector(selectSavedJobsState, (state) => state.loaded);
export const selectSavedJobsError = createSelector(selectSavedJobsState, (state) => state.error);
export const selectSavedJobsPendingOfferIds = createSelector(selectSavedJobsState, (state) => state.pendingOfferIds);

export const selectIsOfferSaved = (offerId: number) =>
  createSelector(selectSavedJobsEntities, (entities) => !!entities[offerId]);

export const selectIsOfferPending = (offerId: number) =>
  createSelector(selectSavedJobsPendingOfferIds, (pending) => pending.includes(offerId));

