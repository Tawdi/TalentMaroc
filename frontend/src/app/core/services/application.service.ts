import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {environment} from '../../../environments/environment';
import {ApiResponse} from '../models/auth.model';
import {
  ApplicationResponse,
  ApplicationStatus,
  CreateApplicationRequest,
  UpdateApplicationStatusRequest,
} from '../models/application.model';
import {Page} from '../models/company-offers.model';

@Injectable({providedIn: 'root'})
export class ApplicationService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/applications/api/v1/applications`;

  // ======================== CANDIDATE ENDPOINTS ========================

  apply(userId: string, request: CreateApplicationRequest): Observable<ApplicationResponse> {
    return this.http
      .post<ApiResponse<ApplicationResponse>>(`${this.apiUrl}/candidate/${userId}`, request)
      .pipe(map((res) => res.data!));
  }

  getMyApplications(userId: string, page = 0, size = 20): Observable<Page<ApplicationResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http
      .get<ApiResponse<Page<ApplicationResponse>>>(`${this.apiUrl}/candidate/${userId}`, {params})
      .pipe(map((res) => res.data!));
  }

  hasApplied(userId: string, offerId: number): Observable<boolean> {
    return this.http
      .get<ApiResponse<boolean>>(`${this.apiUrl}/candidate/${userId}/offers/${offerId}/exists`)
      .pipe(map((res) => res.data ?? false));
  }

  getApplicationForCandidate(userId: string, applicationId: number): Observable<ApplicationResponse> {
    return this.http
      .get<ApiResponse<ApplicationResponse>>(`${this.apiUrl}/candidate/${userId}/${applicationId}`)
      .pipe(map((res) => res.data!));
  }

  withdrawApplication(userId: string, applicationId: number): Observable<ApplicationResponse> {
    return this.http
      .patch<ApiResponse<ApplicationResponse>>(
        `${this.apiUrl}/candidate/${userId}/${applicationId}/withdraw`,
        {},
      )
      .pipe(map((res) => res.data!));
  }

  // ======================== COMPANY ENDPOINTS ========================

  getApplicationsForOffer(
    userId: string,
    offerId: number,
    status?: ApplicationStatus,
    page = 0,
    size = 20,
  ): Observable<Page<ApplicationResponse>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (status) params = params.set('status', status);
    return this.http
      .get<ApiResponse<Page<ApplicationResponse>>>(
        `${this.apiUrl}/company/${userId}/offers/${offerId}`,
        {params},
      )
      .pipe(map((res) => res.data!));
  }

  getApplicationForCompany(userId: string, applicationId: number): Observable<ApplicationResponse> {
    return this.http
      .get<ApiResponse<ApplicationResponse>>(`${this.apiUrl}/company/${userId}/${applicationId}`)
      .pipe(map((res) => res.data!));
  }

  updateApplicationStatus(
    userId: string,
    applicationId: number,
    request: UpdateApplicationStatusRequest,
  ): Observable<ApplicationResponse> {
    return this.http
      .patch<ApiResponse<ApplicationResponse>>(
        `${this.apiUrl}/company/${userId}/${applicationId}/status`,
        request,
      )
      .pipe(map((res) => res.data!));
  }
}
