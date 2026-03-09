// =============================================
// Candidate Profile Models - matching backend DTOs
// =============================================

// API Response wrapper (reuses same structure as auth)
export interface ProfileApiResponse<T> {
  status: 'SUCCESS' | 'ERROR';
  message: string;
  data: T | null;
  timestamp: number;
  path?: string;
  errors?: Record<string, string> | string[];
}

// -------------------- Address --------------------
export interface Address {
  street?: string;
  city: string;
  state?: string;
  zipCode?: string;
  country: string;
}

// -------------------- Skill --------------------
export type SkillLevel = 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED' | 'EXPERT';

export interface Skill {
  id?: number;
  name: string;
  level: SkillLevel;
}

// -------------------- Spoken Language --------------------
export type LanguageProficiency = 'NATIVE' | 'FLUENT' | 'ADVANCED' | 'INTERMEDIATE' | 'BEGINNER';

export interface SpokenLanguage {
  id?: number;
  language: string;
  proficiency: LanguageProficiency;
}

// -------------------- Experience --------------------
export type ContractType =
  | 'CDI'
  | 'CDD'
  | 'STAGE'
  | 'ALTERNANCE'
  | 'FREELANCE'
  | 'INTERIM'
  | 'TEMPS_PARTIEL'
  | 'JOB_ETUDIANT';

export interface Experience {
  id?: number;
  title: string;
  company: string;
  location?: string;
  description?: string;
  contractType: ContractType;
  startDate: string; // ISO date string
  endDate?: string | null;
  currentJob: boolean;
}

// -------------------- Formation --------------------
export interface Formation {
  id?: number;
  institution: string;
  degree: string;
  fieldOfStudy?: string;
  description?: string;
  startDate: string;
  endDate?: string | null;
  currentlyStudying: boolean;
}

// -------------------- Project --------------------
export interface Project {
  id?: number;
  title: string;
  description?: string;
  url?: string;
  technologies?: string;
  startDate?: string;
  endDate?: string | null;
}

// -------------------- Profile --------------------
export interface CandidateProfile {
  id: number;
  userId: string;
  firstName: string;
  lastName: string;
  headline?: string;
  about?: string;
  phone?: string;
  dateOfBirth?: string;
  photoUrl?: string;
  cvOriginalName?: string;
  address?: Address;
  experiences?: Experience[];
  formations?: Formation[];
  skills?: Skill[];
  projects?: Project[];
  spokenLanguages?: SpokenLanguage[];
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateProfileRequest {
  userId: string;
  firstName: string;
  lastName: string;
  headline?: string;
  about?: string;
  phone?: string;
  dateOfBirth?: string;
  address?: Address;
}

export interface UpdateProfileRequest {
  firstName?: string;
  lastName?: string;
  headline?: string;
  about?: string;
  phone?: string;
  dateOfBirth?: string;
  address?: Address;
}

// -------------------- Helpers --------------------
export const SKILL_LEVEL_LABELS: Record<SkillLevel, string> = {
  BEGINNER: 'Beginner',
  INTERMEDIATE: 'Intermediate',
  ADVANCED: 'Advanced',
  EXPERT: 'Expert',
};

export const PROFICIENCY_LABELS: Record<LanguageProficiency, string> = {
  BEGINNER: 'Beginner',
  INTERMEDIATE: 'Intermediate',
  ADVANCED: 'Advanced',
  FLUENT: 'Fluent',
  NATIVE: 'Native',
};

export const CONTRACT_TYPE_LABELS: Record<ContractType, string> = {
  CDI: 'CDI',
  CDD: 'CDD',
  STAGE: 'Internship',
  ALTERNANCE: 'Work-Study',
  FREELANCE: 'Freelance',
  INTERIM: 'Temporary',
  TEMPS_PARTIEL: 'Part-Time',
  JOB_ETUDIANT: 'Student Job',
};

export const SKILL_LEVEL_OPTIONS: { value: SkillLevel; label: string }[] = [
  { value: 'BEGINNER', label: 'Beginner' },
  { value: 'INTERMEDIATE', label: 'Intermediate' },
  { value: 'ADVANCED', label: 'Advanced' },
  { value: 'EXPERT', label: 'Expert' },
];

export const PROFICIENCY_OPTIONS: { value: LanguageProficiency; label: string }[] = [
  { value: 'BEGINNER', label: 'Beginner' },
  { value: 'INTERMEDIATE', label: 'Intermediate' },
  { value: 'ADVANCED', label: 'Advanced' },
  { value: 'FLUENT', label: 'Fluent' },
  { value: 'NATIVE', label: 'Native' },
];

export const CONTRACT_TYPE_OPTIONS: { value: ContractType; label: string }[] = [
  { value: 'CDI', label: 'CDI (Permanent)' },
  { value: 'CDD', label: 'CDD (Fixed-term)' },
  { value: 'STAGE', label: 'Internship' },
  { value: 'ALTERNANCE', label: 'Work-Study' },
  { value: 'FREELANCE', label: 'Freelance' },
  { value: 'INTERIM', label: 'Temporary' },
  { value: 'TEMPS_PARTIEL', label: 'Part-Time' },
  { value: 'JOB_ETUDIANT', label: 'Student Job' },
];

