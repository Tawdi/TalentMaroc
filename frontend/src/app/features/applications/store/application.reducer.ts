import { createReducer, on } from '@ngrx/store';
import { EntityState, EntityAdapter, createEntityAdapter } from '@ngrx/entity';
import { ApplicationResponse } from '../../../core/models/application.model';
import { ApplicationActions } from './application.actions';

// Entity adapters
export const applicationAdapter: EntityAdapter<ApplicationResponse> =
  createEntityAdapter<ApplicationResponse>({
    selectId: (app) => app.id,
    sortComparer: (a, b) => (b.createdAt ?? '').localeCompare(a.createdAt ?? ''),
  });

export interface ApplicationState {
  // Candidate's own applications
  myApplications: EntityState<ApplicationResponse>;
  myTotalElements: number;
  myTotalPages: number;
  myCurrentPage: number;
  myLoading: boolean;

  // Company: applications for a specific offer
  offerApplications: EntityState<ApplicationResponse>;
  offerTotalElements: number;
  offerTotalPages: number;
  offerCurrentPage: number;
  offerLoading: boolean;

  // General
  applying: boolean;
  applySuccess: boolean;
  saving: boolean;
  error: string | null;
}

export const initialApplicationState: ApplicationState = {
  myApplications: applicationAdapter.getInitialState(),
  myTotalElements: 0,
  myTotalPages: 0,
  myCurrentPage: 0,
  myLoading: false,

  offerApplications: applicationAdapter.getInitialState(),
  offerTotalElements: 0,
  offerTotalPages: 0,
  offerCurrentPage: 0,
  offerLoading: false,

  applying: false,
  applySuccess: false,
  saving: false,
  error: null,
};

export const applicationReducer = createReducer(
  initialApplicationState,

  // ======================== APPLY ========================
  on(ApplicationActions.apply, (state) => ({
    ...state,
    applying: true,
    applySuccess: false,
    error: null,
  })),
  on(ApplicationActions.applySuccess, (state, { application }) => ({
    ...state,
    myApplications: applicationAdapter.addOne(application, state.myApplications),
    myTotalElements: state.myTotalElements + 1,
    applying: false,
    applySuccess: true,
  })),
  on(ApplicationActions.applyFailure, (state, { error }) => ({
    ...state,
    applying: false,
    error,
  })),

  // ======================== MY APPLICATIONS ========================
  on(ApplicationActions.loadMyApplications, (state) => ({
    ...state,
    myLoading: true,
    error: null,
  })),
  on(ApplicationActions.loadMyApplicationsSuccess, (state, { applicationsPage }) => ({
    ...state,
    myApplications: applicationAdapter.setAll(applicationsPage.content, state.myApplications),
    myTotalElements: applicationsPage.totalElements,
    myTotalPages: applicationsPage.totalPages,
    myCurrentPage: applicationsPage.number,
    myLoading: false,
  })),
  on(ApplicationActions.loadMyApplicationsFailure, (state, { error }) => ({
    ...state,
    myLoading: false,
    error,
  })),

  // ======================== WITHDRAW ========================
  on(ApplicationActions.withdrawApplication, (state) => ({
    ...state,
    saving: true,
    error: null,
  })),
  on(ApplicationActions.withdrawApplicationSuccess, (state, { application }) => ({
    ...state,
    myApplications: applicationAdapter.updateOne(
      { id: application.id, changes: application },
      state.myApplications,
    ),
    saving: false,
  })),
  on(ApplicationActions.withdrawApplicationFailure, (state, { error }) => ({
    ...state,
    saving: false,
    error,
  })),

  // ======================== OFFER APPLICATIONS ========================
  on(ApplicationActions.loadOfferApplications, (state) => ({
    ...state,
    offerLoading: true,
    error: null,
  })),
  on(ApplicationActions.loadOfferApplicationsSuccess, (state, { applicationsPage }) => ({
    ...state,
    offerApplications: applicationAdapter.setAll(
      applicationsPage.content,
      state.offerApplications,
    ),
    offerTotalElements: applicationsPage.totalElements,
    offerTotalPages: applicationsPage.totalPages,
    offerCurrentPage: applicationsPage.number,
    offerLoading: false,
  })),
  on(ApplicationActions.loadOfferApplicationsFailure, (state, { error }) => ({
    ...state,
    offerLoading: false,
    error,
  })),

  // ======================== UPDATE STATUS ========================
  on(ApplicationActions.updateApplicationStatus, (state) => ({
    ...state,
    saving: true,
    error: null,
  })),
  on(ApplicationActions.updateApplicationStatusSuccess, (state, { application }) => ({
    ...state,
    offerApplications: applicationAdapter.updateOne(
      { id: application.id, changes: application },
      state.offerApplications,
    ),
    saving: false,
  })),
  on(ApplicationActions.updateApplicationStatusFailure, (state, { error }) => ({
    ...state,
    saving: false,
    error,
  })),

  // ======================== CLEAR ========================
  on(ApplicationActions.clearError, (state) => ({
    ...state,
    error: null,
  })),
  on(ApplicationActions.clearApplySuccess, (state) => ({
    ...state,
    applySuccess: false,
  })),
);
