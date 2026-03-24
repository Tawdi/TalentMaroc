import { OfferResponse } from './company-offers.model';

export interface SavedJob {
  id: number;
  offerId: number;
  savedAt: string;
  title?: string;
  companyName?: string;
  location?: string;
  contractType?: string;
  offer?: OfferResponse;
}
