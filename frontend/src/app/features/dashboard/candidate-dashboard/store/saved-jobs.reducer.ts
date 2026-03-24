import { EntityAdapter, EntityState, createEntityAdapter } from '@ngrx/entity';
import { createReducer, on } from '@ngrx/store';
import { SavedJob } from '../../../../core/models/saved-jobs.model';
import { SavedJobsActions } from './saved-jobs.actions';

export const SAVED_JOBS_FEATURE_KEY = 'savedJobs';

export interface SavedJobsState extends EntityState<SavedJob> {
  loading: boolean;
  loaded: boolean;
  pendingOfferIds: number[];
  error: string | null;
}

export const savedJobsAdapter: EntityAdapter<SavedJob> = createEntityAdapter<SavedJob>({
  selectId: (job) => job.offerId,
});

const baseState: Omit<SavedJobsState, keyof EntityState<SavedJob>> = {
  loading: false,
  loaded: false,
  pendingOfferIds: [],
  error: null,
};

export const initialSavedJobsState: SavedJobsState = savedJobsAdapter.getInitialState(baseState);

const removePending = (pending: number[], offerId: number): number[] => pending.filter((id) => id !== offerId);
const addPending = (pending: number[], offerId: number): number[] => (pending.includes(offerId) ? pending : [...pending, offerId]);

export const savedJobsReducer = createReducer(
  initialSavedJobsState,
  on(SavedJobsActions.loadSavedJobs, (state) => ({
    ...state,
    loading: true,
    error: null,
  })),
  on(SavedJobsActions.loadSavedJobsSuccess, (state, { savedJobs }) =>
    savedJobsAdapter.setAll(savedJobs, {
      ...state,
      loading: false,
      loaded: true,
    })
  ),
  on(SavedJobsActions.loadSavedJobsFailure, (state, { error }) => ({
    ...state,
    loading: false,
    loaded: false,
    error,
  })),

  on(SavedJobsActions.saveJob, (state, { offer }) => ({
    ...state,
    pendingOfferIds: addPending(state.pendingOfferIds, offer.id),
    error: null,
  })),
  on(SavedJobsActions.saveJobSuccess, (state, { savedJob }) =>
    savedJobsAdapter.upsertOne(savedJob, {
      ...state,
      pendingOfferIds: removePending(state.pendingOfferIds, savedJob.offerId),
    })
  ),
  on(SavedJobsActions.saveJobFailure, (state, { offerId, error }) => ({
    ...state,
    pendingOfferIds: removePending(state.pendingOfferIds, offerId),
    error,
  })),

  on(SavedJobsActions.removeJob, (state, { offerId }) => ({
    ...state,
    pendingOfferIds: addPending(state.pendingOfferIds, offerId),
    error: null,
  })),
  on(SavedJobsActions.removeJobSuccess, (state, { offerId }) =>
    savedJobsAdapter.removeOne(offerId, {
      ...state,
      pendingOfferIds: removePending(state.pendingOfferIds, offerId),
    })
  ),
  on(SavedJobsActions.removeJobFailure, (state, { offerId, error }) => ({
    ...state,
    pendingOfferIds: removePending(state.pendingOfferIds, offerId),
    error,
  })),

  on(SavedJobsActions.clearSavedJobsError, (state) => ({
    ...state,
    error: null,
  })),
  on(SavedJobsActions.resetSavedJobs, () => initialSavedJobsState)
);

