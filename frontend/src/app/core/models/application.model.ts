// =============================================
// Application Models - matching backend DTOs
// =============================================

import { ApiResponse } from './auth.model';

// -------------------- Application Status --------------------
export type ApplicationStatus = 'RECEIVED' | 'REVIEWING' | 'ACCEPTED' | 'REJECTED' | 'WITHDRAWN';

// -------------------- Offer Summary --------------------
export interface OfferSummary {
  id: number;
  title: string;
  contractType?: string;
  location?: string;
  companyName?: string;
}

// -------------------- Candidate Summary --------------------
export interface CandidateSummary {
  id: number;
  userId: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  city?: string;
}

// -------------------- Application Response --------------------
export interface ApplicationResponse {
  id: number;
  candidateUserId: string;
  offerId: number;
  cvUrl: string;
  coverLetter?: string;
  status: ApplicationStatus;
  companyNote?: string;
  offer?: OfferSummary;
  candidate?: CandidateSummary;
  createdAt?: string;
  updatedAt?: string;
}

// -------------------- Requests --------------------
export interface CreateApplicationRequest {
  offerId: number;
  cvUrl: string;
  coverLetter?: string;
}

export interface UpdateApplicationStatusRequest {
  status: ApplicationStatus;
  companyNote?: string;
}

// -------------------- Labels --------------------
export const APPLICATION_STATUS_LABELS: Record<ApplicationStatus, string> = {
  RECEIVED: 'Received',
  REVIEWING: 'Under Review',
  ACCEPTED: 'Accepted',
  REJECTED: 'Rejected',
  WITHDRAWN: 'Withdrawn',
};

// -------------------- Helpers --------------------
export function getApplicationStatusVariant(
  status: ApplicationStatus,
): 'info' | 'warning' | 'success' | 'error' {
  const map: Record<ApplicationStatus, 'info' | 'warning' | 'success' | 'error'> = {
    RECEIVED: 'info',
    REVIEWING: 'warning',
    ACCEPTED: 'success',
    REJECTED: 'error',
    WITHDRAWN: 'error',
  };
  return map[status];
}

export const APPLICATION_STATUS_OPTIONS: { value: ApplicationStatus; label: string }[] = [
  { value: 'RECEIVED', label: 'Received' },
  { value: 'REVIEWING', label: 'Under Review' },
  { value: 'ACCEPTED', label: 'Accepted' },
  { value: 'REJECTED', label: 'Rejected' },
  { value: 'WITHDRAWN', label: 'Withdrawn' },
];
