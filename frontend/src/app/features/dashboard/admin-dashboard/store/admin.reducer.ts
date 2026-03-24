import { createReducer, on } from '@ngrx/store';
import { AdminActions } from './admin.actions';
import { UserResponse } from '../../../../core/models/auth.model';
import { CompanySummaryResponse } from '../../../../core/models/company-offers.model';

export const ADMIN_FEATURE_KEY = 'admin';

export interface AdminState {
  users: UserResponse[];
  companies: CompanySummaryResponse[];
  loading: boolean;
  error: string | null;
  // User pagination
  usersTotalElements: number;
  usersTotalPages: number;
  usersPage: number;
  usersSize: number;
  // Company pagination
  companiesTotalElements: number;
  companiesTotalPages: number;
  companiesPage: number;
  companiesSize: number;
}

export const initialState: AdminState = {
  users: [],
  companies: [],
  loading: false,
  error: null,
  usersTotalElements: 0,
  usersTotalPages: 0,
  usersPage: 0,
  usersSize: 20,
  companiesTotalElements: 0,
  companiesTotalPages: 0,
  companiesPage: 0,
  companiesSize: 20,
};

export const adminReducer = createReducer(
  initialState,

  // Users
  on(AdminActions.loadUsers, (state) => ({ ...state, loading: true, error: null })),
  on(AdminActions.loadUsersSuccess, (state, { response }) => ({
    ...state,
    loading: false,
    users: response.content,
    usersTotalElements: response.totalElements,
    usersTotalPages: response.totalPages,
    usersPage: response.number,
    usersSize: response.size,
  })),
  on(AdminActions.loadUsersFailure, (state, { error }) => ({ ...state, loading: false, error })),

  on(AdminActions.deleteUser, (state) => ({ ...state, loading: true })),
  on(AdminActions.deleteUserSuccess, (state) => ({ ...state, loading: false })),
  on(AdminActions.deleteUserFailure, (state, { error }) => ({ ...state, loading: false, error })),

  // Companies
  on(AdminActions.loadCompanies, (state) => ({ ...state, loading: true, error: null })),
  on(AdminActions.loadCompaniesSuccess, (state, { response }) => ({
    ...state,
    loading: false,
    companies: response.content,
    companiesTotalElements: response.totalElements,
    companiesTotalPages: response.totalPages,
    companiesPage: response.number,
    companiesSize: response.size,
  })),
  on(AdminActions.loadCompaniesFailure, (state, { error }) => ({ ...state, loading: false, error })),

  on(AdminActions.deleteCompany, (state) => ({ ...state, loading: true })),
  on(AdminActions.deleteCompanySuccess, (state) => ({ ...state, loading: false })),
  on(AdminActions.deleteCompanyFailure, (state, { error }) => ({ ...state, loading: false, error })),

  on(AdminActions.validateCompany, (state) => ({ ...state, loading: true })),
  on(AdminActions.validateCompanySuccess, (state) => ({ ...state, loading: false })),
  on(AdminActions.validateCompanyFailure, (state, { error }) => ({ ...state, loading: false, error })),

  on(AdminActions.clearError, (state) => ({ ...state, error: null }))
);
