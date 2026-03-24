import { createFeatureSelector, createSelector } from '@ngrx/store';
import { AdminState, ADMIN_FEATURE_KEY, initialState } from './admin.reducer';

export const selectAdminState = createFeatureSelector<AdminState>(ADMIN_FEATURE_KEY);

const selectAdminOrInitial = createSelector(selectAdminState, (state): AdminState => state ?? initialState);

export const selectUsers = createSelector(selectAdminOrInitial, (state) => state.users);
export const selectUsersTotalElements = createSelector(selectAdminOrInitial, (state) => state.usersTotalElements);
export const selectUsersTotalPages = createSelector(selectAdminOrInitial, (state) => state.usersTotalPages);
export const selectUsersPage = createSelector(selectAdminOrInitial, (state) => state.usersPage);
export const selectUsersSize = createSelector(selectAdminOrInitial, (state) => state.usersSize);
export const selectUsersLoading = createSelector(selectAdminOrInitial, (state) => state.loading);
export const selectUsersError = createSelector(selectAdminOrInitial, (state) => state.error);

export const selectCompanies = createSelector(selectAdminOrInitial, (state) => state.companies);
export const selectCompaniesTotalElements = createSelector(selectAdminOrInitial, (state) => state.companiesTotalElements);
export const selectCompaniesTotalPages = createSelector(selectAdminOrInitial, (state) => state.companiesTotalPages);
export const selectCompaniesPage = createSelector(selectAdminOrInitial, (state) => state.companiesPage);
export const selectCompaniesSize = createSelector(selectAdminOrInitial, (state) => state.companiesSize);
export const selectCompaniesLoading = createSelector(selectAdminOrInitial, (state) => state.loading);
export const selectCompaniesError = createSelector(selectAdminOrInitial, (state) => state.error);
