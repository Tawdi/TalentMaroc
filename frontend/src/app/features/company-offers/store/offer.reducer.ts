import { createReducer, on } from '@ngrx/store';
import { EntityState, EntityAdapter, createEntityAdapter } from '@ngrx/entity';
import { OfferResponse, Page } from '../../../core/models/company-offers.model';
import { OfferActions } from './offer.actions';

// Entity adapter for my offers
export const offerAdapter: EntityAdapter<OfferResponse> = createEntityAdapter<OfferResponse>({
  selectId: (offer) => offer.id,
  sortComparer: (a, b) => (b.createdAt ?? '').localeCompare(a.createdAt ?? ''),
});

export interface OfferState {
  // Company's own offers
  myOffers: EntityState<OfferResponse>;
  myOffersTotalElements: number;
  myOffersTotalPages: number;
  myOffersCurrentPage: number;
  myOffersLoading: boolean;

  // Public offers
  publicOffers: OfferResponse[];
  publicTotalElements: number;
  publicTotalPages: number;
  publicCurrentPage: number;
  publicLoading: boolean;

  // Selected offer (detail view)
  selectedOffer: OfferResponse | null;
  selectedLoading: boolean;

  // General
  saving: boolean;
  error: string | null;
}

export const initialOfferState: OfferState = {
  myOffers: offerAdapter.getInitialState(),
  myOffersTotalElements: 0,
  myOffersTotalPages: 0,
  myOffersCurrentPage: 0,
  myOffersLoading: false,

  publicOffers: [],
  publicTotalElements: 0,
  publicTotalPages: 0,
  publicCurrentPage: 0,
  publicLoading: false,

  selectedOffer: null,
  selectedLoading: false,

  saving: false,
  error: null,
};

export const offerReducer = createReducer(
  initialOfferState,

  // ======================== MY OFFERS ========================
  on(OfferActions.loadMyOffers, (state) => ({
    ...state,
    myOffersLoading: true,
    error: null,
  })),
  on(OfferActions.loadMyOffersSuccess, (state, { offersPage }) => ({
    ...state,
    myOffers: offerAdapter.setAll(offersPage.content, state.myOffers),
    myOffersTotalElements: offersPage.totalElements,
    myOffersTotalPages: offersPage.totalPages,
    myOffersCurrentPage: offersPage.number,
    myOffersLoading: false,
  })),
  on(OfferActions.loadMyOffersFailure, (state, { error }) => ({
    ...state,
    myOffersLoading: false,
    error,
  })),

  // ======================== CREATE OFFER ========================
  on(OfferActions.createOffer, (state) => ({
    ...state,
    saving: true,
    error: null,
  })),
  on(OfferActions.createOfferSuccess, (state, { offer }) => ({
    ...state,
    myOffers: offerAdapter.addOne(offer, state.myOffers),
    myOffersTotalElements: state.myOffersTotalElements + 1,
    saving: false,
  })),
  on(OfferActions.createOfferFailure, (state, { error }) => ({
    ...state,
    saving: false,
    error,
  })),

  // ======================== UPDATE OFFER ========================
  on(OfferActions.updateOffer, (state) => ({
    ...state,
    saving: true,
    error: null,
  })),
  on(OfferActions.updateOfferSuccess, (state, { offer }) => ({
    ...state,
    myOffers: offerAdapter.updateOne({ id: offer.id, changes: offer }, state.myOffers),
    saving: false,
  })),
  on(OfferActions.updateOfferFailure, (state, { error }) => ({
    ...state,
    saving: false,
    error,
  })),

  // ======================== DELETE OFFER ========================
  on(OfferActions.deleteOffer, (state) => ({
    ...state,
    saving: true,
    error: null,
  })),
  on(OfferActions.deleteOfferSuccess, (state, { offerId }) => ({
    ...state,
    myOffers: offerAdapter.removeOne(offerId, state.myOffers),
    myOffersTotalElements: state.myOffersTotalElements - 1,
    saving: false,
  })),
  on(OfferActions.deleteOfferFailure, (state, { error }) => ({
    ...state,
    saving: false,
    error,
  })),

  // ======================== STATUS TRANSITIONS ========================
  on(OfferActions.publishOffer, OfferActions.closeOffer, OfferActions.archiveOffer, (state) => ({
    ...state,
    saving: true,
    error: null,
  })),
  on(OfferActions.statusChangeSuccess, (state, { offer }) => ({
    ...state,
    myOffers: offerAdapter.updateOne({ id: offer.id, changes: offer }, state.myOffers),
    saving: false,
  })),
  on(OfferActions.statusChangeFailure, (state, { error }) => ({
    ...state,
    saving: false,
    error,
  })),

  // ======================== PUBLIC OFFERS ========================
  on(OfferActions.loadActiveOffers, OfferActions.filterOffers, (state) => ({
    ...state,
    publicLoading: true,
    error: null,
  })),
  on(OfferActions.loadActiveOffersSuccess, OfferActions.filterOffersSuccess, (state, { offersPage }) => ({
    ...state,
    publicOffers: offersPage.content,
    publicTotalElements: offersPage.totalElements,
    publicTotalPages: offersPage.totalPages,
    publicCurrentPage: offersPage.number,
    publicLoading: false,
  })),
  on(OfferActions.loadActiveOffersFailure, OfferActions.filterOffersFailure, (state, { error }) => ({
    ...state,
    publicLoading: false,
    error,
  })),

  // ======================== SELECTED OFFER ========================
  on(OfferActions.loadOfferById, (state) => ({
    ...state,
    selectedOffer: null,
    selectedLoading: true,
    error: null,
  })),
  on(OfferActions.loadOfferByIdSuccess, (state, { offer }) => ({
    ...state,
    selectedOffer: offer,
    selectedLoading: false,
  })),
  on(OfferActions.loadOfferByIdFailure, (state, { error }) => ({
    ...state,
    selectedLoading: false,
    error,
  })),

  // ======================== CLEAR ========================
  on(OfferActions.clearError, (state) => ({
    ...state,
    error: null,
  })),
  on(OfferActions.clearSelectedOffer, (state) => ({
    ...state,
    selectedOffer: null,
  })),
);

