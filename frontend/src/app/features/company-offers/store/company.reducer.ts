import { createReducer, on } from '@ngrx/store';
import { CompanyResponse, CompanySummaryResponse } from '../../../core/models/company-offers.model';
import { CompanyActions } from './company.actions';

export interface CompanyState {
  company: CompanyResponse | null;
  loading: boolean;
  error: string | null;
  notFound: boolean;
  pendingCompanies: CompanySummaryResponse[];
  pendingLoading: boolean;
  pendingError: string | null;
  saving: boolean;
}

export const initialCompanyState: CompanyState = {
  company: null,
  loading: false,
  error: null,
  notFound: false,
  pendingCompanies: [],
  pendingLoading: false,
  pendingError: null,
  saving: false,
};

export const companyReducer = createReducer(
  initialCompanyState,

  // Load company
  on(CompanyActions.loadCompany, (state) => ({
    ...state,
    loading: true,
    error: null,
    notFound: false,
  })),
  on(CompanyActions.loadCompanySuccess, (state, { company }) => ({
    ...state,
    company,
    loading: false,
    notFound: false,
  })),
  on(CompanyActions.loadCompanyFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error,
  })),
  on(CompanyActions.loadCompanyNotFound, (state) => ({
    ...state,
    loading: false,
    notFound: true,
    company: null,
  })),

  // Create company
  on(CompanyActions.createCompany, (state) => ({
    ...state,
    saving: true,
    error: null,
  })),
  on(CompanyActions.createCompanySuccess, (state, { company }) => ({
    ...state,
    company,
    saving: false,
    notFound: false,
  })),
  on(CompanyActions.createCompanyFailure, (state, { error }) => ({
    ...state,
    saving: false,
    error,
  })),

  // Update company
  on(CompanyActions.updateCompany, (state) => ({
    ...state,
    saving: true,
    error: null,
  })),
  on(CompanyActions.updateCompanySuccess, (state, { company }) => ({
    ...state,
    company,
    saving: false,
  })),
  on(CompanyActions.updateCompanyFailure, (state, { error }) => ({
    ...state,
    saving: false,
    error,
  })),

  // Delete company
  on(CompanyActions.deleteCompany, (state) => ({
    ...state,
    saving: true,
    error: null,
  })),
  on(CompanyActions.deleteCompanySuccess, (state) => ({
    ...state,
    company: null,
    saving: false,
    notFound: true,
  })),
  on(CompanyActions.deleteCompanyFailure, (state, { error }) => ({
    ...state,
    saving: false,
    error,
  })),

  // Admin: pending companies
  on(CompanyActions.loadPendingCompanies, (state) => ({
    ...state,
    pendingLoading: true,
    pendingError: null,
  })),
  on(CompanyActions.loadPendingCompaniesSuccess, (state, { companies }) => ({
    ...state,
    pendingCompanies: companies,
    pendingLoading: false,
  })),
  on(CompanyActions.loadPendingCompaniesFailure, (state, { error }) => ({
    ...state,
    pendingLoading: false,
    pendingError: error,
  })),

  // Admin: validate company
  on(CompanyActions.validateCompany, (state) => ({
    ...state,
    saving: true,
    pendingError: null,
  })),
  on(CompanyActions.validateCompanySuccess, (state, { company }) => ({
    ...state,
    saving: false,
    pendingCompanies: state.pendingCompanies.filter((c) => c.id !== company.id),
  })),
  on(CompanyActions.validateCompanyFailure, (state, { error }) => ({
    ...state,
    saving: false,
    pendingError: error,
  })),

  // Clear error
  on(CompanyActions.clearError, (state) => ({
    ...state,
    error: null,
    pendingError: null,
  })),
);

