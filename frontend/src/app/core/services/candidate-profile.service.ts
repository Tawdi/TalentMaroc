import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import {
  ProfileApiResponse,
  CandidateProfile,
  CreateProfileRequest,
  UpdateProfileRequest,
  Skill,
  SpokenLanguage,
  Experience,
  Formation,
  Project,
} from '../models/profile.model';

@Injectable({ providedIn: 'root' })
export class CandidateProfileService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/candidate-profile/api/v1/profiles`;

  // ======================== PROFILE ========================

  getProfile(userId: string): Observable<CandidateProfile> {
    return this.http
      .get<ProfileApiResponse<CandidateProfile>>(`${this.apiUrl}/user/${userId}`)
      .pipe(map((res) => res.data!));
  }

  createProfile(request: CreateProfileRequest): Observable<CandidateProfile> {
    return this.http
      .post<ProfileApiResponse<CandidateProfile>>(this.apiUrl, request)
      .pipe(map((res) => res.data!));
  }

  updateProfile(userId: string, request: UpdateProfileRequest): Observable<CandidateProfile> {
    return this.http
      .put<ProfileApiResponse<CandidateProfile>>(`${this.apiUrl}/user/${userId}`, request)
      .pipe(map((res) => res.data!));
  }

  deleteProfile(userId: string): Observable<void> {
    return this.http
      .delete<ProfileApiResponse<void>>(`${this.apiUrl}/user/${userId}`)
      .pipe(map(() => void 0));
  }

  // ======================== CV ========================

  uploadCv(userId: string, file: File): Observable<CandidateProfile> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http
      .post<ProfileApiResponse<CandidateProfile>>(`${this.apiUrl}/user/${userId}/cv`, formData)
      .pipe(map((res) => res.data!));
  }

  downloadCv(userId: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/user/${userId}/cv`, { responseType: 'blob' });
  }

  deleteCv(userId: string): Observable<void> {
    return this.http
      .delete<ProfileApiResponse<void>>(`${this.apiUrl}/user/${userId}/cv`)
      .pipe(map(() => void 0));
  }

  // ======================== SKILLS ========================

  getSkills(userId: string): Observable<Skill[]> {
    return this.http
      .get<ProfileApiResponse<Skill[]>>(`${this.apiUrl}/user/${userId}/skills`)
      .pipe(map((res) => res.data ?? []));
  }

  createSkill(userId: string, skill: Skill): Observable<Skill> {
    return this.http
      .post<ProfileApiResponse<Skill>>(`${this.apiUrl}/user/${userId}/skills`, skill)
      .pipe(map((res) => res.data!));
  }

  updateSkill(userId: string, skillId: number, skill: Skill): Observable<Skill> {
    return this.http
      .put<ProfileApiResponse<Skill>>(`${this.apiUrl}/user/${userId}/skills/${skillId}`, skill)
      .pipe(map((res) => res.data!));
  }

  deleteSkill(userId: string, skillId: number): Observable<void> {
    return this.http
      .delete<ProfileApiResponse<void>>(`${this.apiUrl}/user/${userId}/skills/${skillId}`)
      .pipe(map(() => void 0));
  }

  // ======================== LANGUAGES ========================

  getLanguages(userId: string): Observable<SpokenLanguage[]> {
    return this.http
      .get<ProfileApiResponse<SpokenLanguage[]>>(`${this.apiUrl}/user/${userId}/languages`)
      .pipe(map((res) => res.data ?? []));
  }

  createLanguage(userId: string, lang: SpokenLanguage): Observable<SpokenLanguage> {
    return this.http
      .post<ProfileApiResponse<SpokenLanguage>>(`${this.apiUrl}/user/${userId}/languages`, lang)
      .pipe(map((res) => res.data!));
  }

  updateLanguage(userId: string, langId: number, lang: SpokenLanguage): Observable<SpokenLanguage> {
    return this.http
      .put<ProfileApiResponse<SpokenLanguage>>(`${this.apiUrl}/user/${userId}/languages/${langId}`, lang)
      .pipe(map((res) => res.data!));
  }

  deleteLanguage(userId: string, langId: number): Observable<void> {
    return this.http
      .delete<ProfileApiResponse<void>>(`${this.apiUrl}/user/${userId}/languages/${langId}`)
      .pipe(map(() => void 0));
  }

  // ======================== EXPERIENCES ========================

  getExperiences(userId: string): Observable<Experience[]> {
    return this.http
      .get<ProfileApiResponse<Experience[]>>(`${this.apiUrl}/user/${userId}/experiences`)
      .pipe(map((res) => res.data ?? []));
  }

  createExperience(userId: string, exp: Experience): Observable<Experience> {
    return this.http
      .post<ProfileApiResponse<Experience>>(`${this.apiUrl}/user/${userId}/experiences`, exp)
      .pipe(map((res) => res.data!));
  }

  updateExperience(userId: string, expId: number, exp: Experience): Observable<Experience> {
    return this.http
      .put<ProfileApiResponse<Experience>>(`${this.apiUrl}/user/${userId}/experiences/${expId}`, exp)
      .pipe(map((res) => res.data!));
  }

  deleteExperience(userId: string, expId: number): Observable<void> {
    return this.http
      .delete<ProfileApiResponse<void>>(`${this.apiUrl}/user/${userId}/experiences/${expId}`)
      .pipe(map(() => void 0));
  }

  // ======================== FORMATIONS ========================

  getFormations(userId: string): Observable<Formation[]> {
    return this.http
      .get<ProfileApiResponse<Formation[]>>(`${this.apiUrl}/user/${userId}/formations`)
      .pipe(map((res) => res.data ?? []));
  }

  createFormation(userId: string, f: Formation): Observable<Formation> {
    return this.http
      .post<ProfileApiResponse<Formation>>(`${this.apiUrl}/user/${userId}/formations`, f)
      .pipe(map((res) => res.data!));
  }

  updateFormation(userId: string, fId: number, f: Formation): Observable<Formation> {
    return this.http
      .put<ProfileApiResponse<Formation>>(`${this.apiUrl}/user/${userId}/formations/${fId}`, f)
      .pipe(map((res) => res.data!));
  }

  deleteFormation(userId: string, fId: number): Observable<void> {
    return this.http
      .delete<ProfileApiResponse<void>>(`${this.apiUrl}/user/${userId}/formations/${fId}`)
      .pipe(map(() => void 0));
  }

  // ======================== PROJECTS ========================

  getProjects(userId: string): Observable<Project[]> {
    return this.http
      .get<ProfileApiResponse<Project[]>>(`${this.apiUrl}/user/${userId}/projects`)
      .pipe(map((res) => res.data ?? []));
  }

  createProject(userId: string, p: Project): Observable<Project> {
    return this.http
      .post<ProfileApiResponse<Project>>(`${this.apiUrl}/user/${userId}/projects`, p)
      .pipe(map((res) => res.data!));
  }

  updateProject(userId: string, pId: number, p: Project): Observable<Project> {
    return this.http
      .put<ProfileApiResponse<Project>>(`${this.apiUrl}/user/${userId}/projects/${pId}`, p)
      .pipe(map((res) => res.data!));
  }

  deleteProject(userId: string, pId: number): Observable<void> {
    return this.http
      .delete<ProfileApiResponse<void>>(`${this.apiUrl}/user/${userId}/projects/${pId}`)
      .pipe(map(() => void 0));
  }
}

