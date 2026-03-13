import { ActionReducerMap, createFeatureSelector, createSelector } from '@ngrx/store';
import { CompanyState, companyReducer } from './company.reducer';
import { OfferState, offerReducer, offerAdapter } from './offer.reducer';

// Feature key
export const COMPANY_OFFERS_FEATURE_KEY = 'companyOffers';

// Combined state
export interface CompanyOffersState {
  company: CompanyState;
  offer: OfferState;
}

// Combined reducer map
export const companyOffersReducers: ActionReducerMap<CompanyOffersState> = {
  company: companyReducer,
  offer: offerReducer,
};

// ======================== Feature Selector ========================
const selectCompanyOffersState = createFeatureSelector<CompanyOffersState>(COMPANY_OFFERS_FEATURE_KEY);

// ======================== Company Selectors ========================
const selectCompanyState = createSelector(selectCompanyOffersState, (s) => s.company);

export const selectCompany = createSelector(selectCompanyState, (s) => s.company);
export const selectCompanyLoading = createSelector(selectCompanyState, (s) => s.loading);
export const selectCompanyError = createSelector(selectCompanyState, (s) => s.error);
export const selectCompanyNotFound = createSelector(selectCompanyState, (s) => s.notFound);
export const selectCompanySaving = createSelector(selectCompanyState, (s) => s.saving);
export const selectPendingCompanies = createSelector(selectCompanyState, (s) => s.pendingCompanies);
export const selectPendingLoading = createSelector(selectCompanyState, (s) => s.pendingLoading);
export const selectPendingError = createSelector(selectCompanyState, (s) => s.pendingError);

// ======================== Offer Selectors ========================
const selectOfferState = createSelector(selectCompanyOffersState, (s) => s.offer);

const { selectAll: selectAllMyOffersEntities } = offerAdapter.getSelectors();

export const selectMyOffers = createSelector(selectOfferState, (s) => selectAllMyOffersEntities(s.myOffers));
export const selectMyOffersLoading = createSelector(selectOfferState, (s) => s.myOffersLoading);
export const selectMyOffersTotalElements = createSelector(selectOfferState, (s) => s.myOffersTotalElements);
export const selectMyOffersTotalPages = createSelector(selectOfferState, (s) => s.myOffersTotalPages);
export const selectMyOffersCurrentPage = createSelector(selectOfferState, (s) => s.myOffersCurrentPage);

export const selectPublicOffers = createSelector(selectOfferState, (s) => s.publicOffers);
export const selectPublicLoading = createSelector(selectOfferState, (s) => s.publicLoading);
export const selectPublicTotalElements = createSelector(selectOfferState, (s) => s.publicTotalElements);
export const selectPublicTotalPages = createSelector(selectOfferState, (s) => s.publicTotalPages);
export const selectPublicCurrentPage = createSelector(selectOfferState, (s) => s.publicCurrentPage);

export const selectSelectedOffer = createSelector(selectOfferState, (s) => s.selectedOffer);
export const selectSelectedLoading = createSelector(selectOfferState, (s) => s.selectedLoading);

export const selectOfferSaving = createSelector(selectOfferState, (s) => s.saving);
export const selectOfferError = createSelector(selectOfferState, (s) => s.error);

// Re-exports
export { CompanyActions } from './company.actions';
export { OfferActions } from './offer.actions';
export { CompanyEffects } from './company.effects';
export { OfferEffects } from './offer.effects';

