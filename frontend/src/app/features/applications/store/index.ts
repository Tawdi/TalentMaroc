import { ActionReducerMap, createFeatureSelector, createSelector } from '@ngrx/store';
import {
  ApplicationState,
  applicationReducer,
  applicationAdapter,
} from './application.reducer';

// Feature key
export const APPLICATIONS_FEATURE_KEY = 'applications';

// Combined state (single reducer for now, but follows pattern for expansion)
export interface ApplicationsFeatureState {
  application: ApplicationState;
}

// Combined reducer map
export const applicationsReducers: ActionReducerMap<ApplicationsFeatureState> = {
  application: applicationReducer,
};

// ======================== Feature Selector ========================
const selectApplicationsFeature =
  createFeatureSelector<ApplicationsFeatureState>(APPLICATIONS_FEATURE_KEY);

const selectApplicationState = createSelector(selectApplicationsFeature, (s) => s.application);

// ======================== Candidate Selectors ========================
const { selectAll: selectAllMyApplications } = applicationAdapter.getSelectors();

export const selectMyApplications = createSelector(selectApplicationState, (s) =>
  selectAllMyApplications(s.myApplications),
);
export const selectMyLoading = createSelector(selectApplicationState, (s) => s.myLoading);
export const selectMyTotalElements = createSelector(selectApplicationState, (s) => s.myTotalElements);
export const selectMyTotalPages = createSelector(selectApplicationState, (s) => s.myTotalPages);
export const selectMyCurrentPage = createSelector(selectApplicationState, (s) => s.myCurrentPage);

// ======================== Company Selectors ========================
const { selectAll: selectAllOfferApplications } = applicationAdapter.getSelectors();

export const selectOfferApplications = createSelector(selectApplicationState, (s) =>
  selectAllOfferApplications(s.offerApplications),
);
export const selectOfferLoading = createSelector(selectApplicationState, (s) => s.offerLoading);
export const selectOfferTotalElements = createSelector(
  selectApplicationState,
  (s) => s.offerTotalElements,
);
export const selectOfferTotalPages = createSelector(
  selectApplicationState,
  (s) => s.offerTotalPages,
);
export const selectOfferCurrentPage = createSelector(
  selectApplicationState,
  (s) => s.offerCurrentPage,
);

// ======================== General Selectors ========================
export const selectApplying = createSelector(selectApplicationState, (s) => s.applying);
export const selectApplySuccess = createSelector(selectApplicationState, (s) => s.applySuccess);
export const selectSaving = createSelector(selectApplicationState, (s) => s.saving);
export const selectApplicationError = createSelector(selectApplicationState, (s) => s.error);

// Re-exports
export { ApplicationActions } from './application.actions';
export { ApplicationEffects } from './application.effects';
