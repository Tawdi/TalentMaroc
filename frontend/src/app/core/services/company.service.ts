import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/auth.model';
import {
  CompanyResponse,
  CompanySummaryResponse,
  CreateCompanyRequest,
  UpdateCompanyRequest,
  ValidateCompanyRequest,
  Page,
} from '../models/company-offers.model';

@Injectable({ providedIn: 'root' })
export class CompanyService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/company-offers/api/v1/companies`;
  private readonly adminUrl = `${environment.apiUrl}/company-offers/api/v1/admin/companies`;

  // ======================== COMPANY CRUD ========================

  createCompany(request: CreateCompanyRequest): Observable<CompanyResponse> {
    return this.http
      .post<ApiResponse<CompanyResponse>>(this.apiUrl, request)
      .pipe(map((res) => res.data!));
  }

  getCompanyByUserId(userId: string): Observable<CompanyResponse> {
    return this.http
      .get<ApiResponse<CompanyResponse>>(`${this.apiUrl}/user/${userId}`)
      .pipe(map((res) => res.data!));
  }

  getCompanyById(companyId: number): Observable<CompanyResponse> {
    return this.http
      .get<ApiResponse<CompanyResponse>>(`${this.apiUrl}/${companyId}`)
      .pipe(map((res) => res.data!));
  }

  updateCompany(userId: string, request: UpdateCompanyRequest): Observable<CompanyResponse> {
    return this.http
      .put<ApiResponse<CompanyResponse>>(`${this.apiUrl}/user/${userId}`, request)
      .pipe(map((res) => res.data!));
  }

  deleteCompany(userId: string): Observable<void> {
    return this.http
      .delete<ApiResponse<void>>(`${this.apiUrl}/user/${userId}`)
      .pipe(map(() => void 0));
  }

  // ======================== PUBLIC LISTING ========================

  getApprovedCompanies(page = 0, size = 20): Observable<Page<CompanySummaryResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http
      .get<ApiResponse<Page<CompanySummaryResponse>>>(this.apiUrl, { params })
      .pipe(map((res) => res.data!));
  }

  // ======================== ADMIN ========================

  getPendingCompanies(): Observable<CompanySummaryResponse[]> {
    return this.http
      .get<ApiResponse<CompanySummaryResponse[]>>(`${this.adminUrl}/pending`)
      .pipe(map((res) => res.data ?? []));
  }

  getAllCompanies(page = 0, size = 20): Observable<Page<CompanySummaryResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http
      .get<ApiResponse<Page<CompanySummaryResponse>>>(`${this.adminUrl}`, { params })
      .pipe(map((res) => res.data!));
  }

  deleteCompanyById(companyId: number): Observable<void> {
    return this.http
      .delete<ApiResponse<void>>(`${this.adminUrl}/${companyId}`)
      .pipe(map(() => void 0));
  }

  validateCompany(companyId: number, request: ValidateCompanyRequest): Observable<CompanyResponse> {
    return this.http
      .put<ApiResponse<CompanyResponse>>(`${this.adminUrl}/${companyId}/validate`, request)
      .pipe(map((res) => res.data!));
  }
}
