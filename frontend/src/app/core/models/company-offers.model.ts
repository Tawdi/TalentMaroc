// =============================================
// Company & Offers Models - matching backend DTOs
// =============================================

import { ApiResponse } from './auth.model';

// -------------------- Pagination --------------------
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number; // current page (0-based)
  size: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

// -------------------- Address --------------------
export interface CompanyAddress {
  street?: string;
  city?: string;
  state?: string;
  zipCode?: string;
  country?: string;
}

// -------------------- Company Status --------------------
export type CompanyStatus = 'PENDING' | 'APPROVED' | 'REJECTED';

// -------------------- Offer Status --------------------
export type OfferStatus = 'DRAFT' | 'ACTIVE' | 'CLOSED' | 'ARCHIVED';

// -------------------- Offer Contract Type --------------------
export type OfferContractType =
  | 'CDI'
  | 'CDD'
  | 'STAGE'
  | 'ALTERNANCE'
  | 'FREELANCE'
  | 'INTERIM'
  | 'TEMPS_PARTIEL'
  | 'JOB_ETUDIANT';

// -------------------- Company Summary --------------------
export interface CompanySummaryResponse {
  id: number;
  companyName: string;
  sector: string;
  logoUrl?: string;
  status: CompanyStatus;
  address?: CompanyAddress;
  createdAt?: string;
}

// -------------------- Company Response --------------------
export interface CompanyResponse {
  id: number;
  userId: string;
  companyName: string;
  sector: string;
  description?: string;
  website?: string;
  phone?: string;
  logoUrl?: string;
  status: CompanyStatus;
  validatedAt?: string;
  address?: CompanyAddress;
  offers?: OfferResponse[];
  createdAt?: string;
  updatedAt?: string;
}

// -------------------- Offer Response --------------------
export interface OfferResponse {
  id: number;
  title: string;
  description: string;
  contractType: OfferContractType;
  location?: string;
  salaryRange?: string;
  requirements?: string;
  benefits?: string;
  status: OfferStatus;
  expiresAt?: string;
  viewsCount?: number;
  applicationsCount?: number;
  company?: CompanySummaryResponse;
  createdAt?: string;
  updatedAt?: string;
}

// -------------------- Requests --------------------
export interface CreateCompanyRequest {
  userId: string;
  companyName: string;
  sector: string;
  description?: string;
  website?: string;
  phone?: string;
  address?: CompanyAddress;
}

export interface UpdateCompanyRequest {
  companyName?: string;
  sector?: string;
  description?: string;
  website?: string;
  phone?: string;
  address?: CompanyAddress;
}

export interface CreateOfferRequest {
  title: string;
  description: string;
  contractType: OfferContractType;
  location?: string;
  salaryRange?: string;
  requirements?: string;
  benefits?: string;
  expiresAt?: string;
}

export interface UpdateOfferRequest {
  title?: string;
  description?: string;
  contractType?: OfferContractType;
  location?: string;
  salaryRange?: string;
  requirements?: string;
  benefits?: string;
  status?: OfferStatus;
  expiresAt?: string;
}

export interface ValidateCompanyRequest {
  status: CompanyStatus;
  reason?: string;
}

// -------------------- Labels / Options --------------------
export const COMPANY_STATUS_LABELS: Record<CompanyStatus, string> = {
  PENDING: 'Pending Review',
  APPROVED: 'Approved',
  REJECTED: 'Rejected',
};

export const OFFER_STATUS_LABELS: Record<OfferStatus, string> = {
  DRAFT: 'Draft',
  ACTIVE: 'Active',
  CLOSED: 'Closed',
  ARCHIVED: 'Archived',
};

export const OFFER_CONTRACT_TYPE_LABELS: Record<OfferContractType, string> = {
  CDI: 'CDI (Permanent)',
  CDD: 'CDD (Fixed-term)',
  STAGE: 'Internship',
  ALTERNANCE: 'Work-Study',
  FREELANCE: 'Freelance',
  INTERIM: 'Temporary',
  TEMPS_PARTIEL: 'Part-Time',
  JOB_ETUDIANT: 'Student Job',
};

export const OFFER_CONTRACT_TYPE_OPTIONS: { value: OfferContractType; label: string }[] = [
  { value: 'CDI', label: 'CDI (Permanent)' },
  { value: 'CDD', label: 'CDD (Fixed-term)' },
  { value: 'STAGE', label: 'Internship' },
  { value: 'ALTERNANCE', label: 'Work-Study' },
  { value: 'FREELANCE', label: 'Freelance' },
  { value: 'INTERIM', label: 'Temporary' },
  { value: 'TEMPS_PARTIEL', label: 'Part-Time' },
  { value: 'JOB_ETUDIANT', label: 'Student Job' },
];

export const OFFER_STATUS_OPTIONS: { value: OfferStatus; label: string }[] = [
  { value: 'DRAFT', label: 'Draft' },
  { value: 'ACTIVE', label: 'Active' },
  { value: 'CLOSED', label: 'Closed' },
  { value: 'ARCHIVED', label: 'Archived' },
];

export const COMPANY_STATUS_OPTIONS: { value: CompanyStatus; label: string }[] = [
  { value: 'APPROVED', label: 'Approve' },
  { value: 'REJECTED', label: 'Reject' },
];

// -------------------- Helper functions --------------------
export function getCompanyStatusVariant(status: CompanyStatus): 'warning' | 'success' | 'error' {
  const map: Record<CompanyStatus, 'warning' | 'success' | 'error'> = {
    PENDING: 'warning',
    APPROVED: 'success',
    REJECTED: 'error',
  };
  return map[status];
}

export function getOfferStatusVariant(status: OfferStatus): 'info' | 'success' | 'warning' | 'error' {
  const map: Record<OfferStatus, 'info' | 'success' | 'warning' | 'error'> = {
    DRAFT: 'info',
    ACTIVE: 'success',
    CLOSED: 'warning',
    ARCHIVED: 'error',
  };
  return map[status];
}

