import { createActionGroup, emptyProps, props } from '@ngrx/store';
import {
  OfferResponse,
  CreateOfferRequest,
  UpdateOfferRequest,
  OfferContractType,
  Page,
} from '../../../core/models/company-offers.model';

export const OfferActions = createActionGroup({
  source: 'Offer',
  events: {
    // My offers (company owner)
    'Load My Offers': props<{ userId: string; page?: number; size?: number }>(),
    'Load My Offers Success': props<{ offersPage: Page<OfferResponse> }>(),
    'Load My Offers Failure': props<{ error: string }>(),

    // Create offer
    'Create Offer': props<{ userId: string; request: CreateOfferRequest }>(),
    'Create Offer Success': props<{ offer: OfferResponse }>(),
    'Create Offer Failure': props<{ error: string }>(),

    // Update offer
    'Update Offer': props<{ userId: string; offerId: number; request: UpdateOfferRequest }>(),
    'Update Offer Success': props<{ offer: OfferResponse }>(),
    'Update Offer Failure': props<{ error: string }>(),

    // Delete offer
    'Delete Offer': props<{ userId: string; offerId: number }>(),
    'Delete Offer Success': props<{ offerId: number }>(),
    'Delete Offer Failure': props<{ error: string }>(),

    // Status transitions
    'Publish Offer': props<{ userId: string; offerId: number }>(),
    'Close Offer': props<{ userId: string; offerId: number }>(),
    'Archive Offer': props<{ userId: string; offerId: number }>(),
    'Status Change Success': props<{ offer: OfferResponse }>(),
    'Status Change Failure': props<{ error: string }>(),

    // Public: active offers
    'Load Active Offers': props<{ page?: number; size?: number }>(),
    'Load Active Offers Success': props<{ offersPage: Page<OfferResponse> }>(),
    'Load Active Offers Failure': props<{ error: string }>(),

    // Public: single offer
    'Load Offer By Id': props<{ offerId: number }>(),
    'Load Offer By Id Success': props<{ offer: OfferResponse }>(),
    'Load Offer By Id Failure': props<{ error: string }>(),

    // Public: filter offers
    'Filter Offers': props<{
      filters: { keyword?: string; location?: string; contractType?: OfferContractType };
      page?: number;
      size?: number;
    }>(),
    'Filter Offers Success': props<{ offersPage: Page<OfferResponse> }>(),
    'Filter Offers Failure': props<{ error: string }>(),

    // Clear
    'Clear Error': emptyProps(),
    'Clear Selected Offer': emptyProps(),
  },
});

