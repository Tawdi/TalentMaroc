import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/auth.model';
import {
  OfferResponse,
  CreateOfferRequest,
  UpdateOfferRequest,
  OfferContractType,
  Page,
} from '../models/company-offers.model';

@Injectable({ providedIn: 'root' })
export class OfferService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/company-offers/api/v1/offers`;

  // ======================== MY OFFERS (company owner) ========================

  createOffer(userId: string, request: CreateOfferRequest): Observable<OfferResponse> {
    return this.http
      .post<ApiResponse<OfferResponse>>(`${this.apiUrl}/user/${userId}`, request)
      .pipe(map((res) => res.data!));
  }

  getMyOffers(userId: string, page = 0, size = 20): Observable<Page<OfferResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http
      .get<ApiResponse<Page<OfferResponse>>>(`${this.apiUrl}/user/${userId}`, { params })
      .pipe(map((res) => res.data!));
  }

  updateOffer(userId: string, offerId: number, request: UpdateOfferRequest): Observable<OfferResponse> {
    return this.http
      .put<ApiResponse<OfferResponse>>(`${this.apiUrl}/user/${userId}/${offerId}`, request)
      .pipe(map((res) => res.data!));
  }

  deleteOffer(userId: string, offerId: number): Observable<void> {
    return this.http
      .delete<ApiResponse<void>>(`${this.apiUrl}/user/${userId}/${offerId}`)
      .pipe(map(() => void 0));
  }

  // ======================== STATUS TRANSITIONS ========================

  publishOffer(userId: string, offerId: number): Observable<OfferResponse> {
    return this.http
      .patch<ApiResponse<OfferResponse>>(`${this.apiUrl}/user/${userId}/${offerId}/publish`, {})
      .pipe(map((res) => res.data!));
  }

  closeOffer(userId: string, offerId: number): Observable<OfferResponse> {
    return this.http
      .patch<ApiResponse<OfferResponse>>(`${this.apiUrl}/user/${userId}/${offerId}/close`, {})
      .pipe(map((res) => res.data!));
  }

  archiveOffer(userId: string, offerId: number): Observable<OfferResponse> {
    return this.http
      .patch<ApiResponse<OfferResponse>>(`${this.apiUrl}/user/${userId}/${offerId}/archive`, {})
      .pipe(map((res) => res.data!));
  }

  // ======================== PUBLIC ENDPOINTS ========================

  getActiveOffers(page = 0, size = 20): Observable<Page<OfferResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http
      .get<ApiResponse<Page<OfferResponse>>>(this.apiUrl, { params })
      .pipe(map((res) => res.data!));
  }

  getOfferById(offerId: number): Observable<OfferResponse> {
    return this.http
      .get<ApiResponse<OfferResponse>>(`${this.apiUrl}/${offerId}`)
      .pipe(map((res) => res.data!));
  }

  searchOffers(keyword: string, page = 0, size = 20): Observable<Page<OfferResponse>> {
    const params = new HttpParams().set('keyword', keyword).set('page', page).set('size', size);
    return this.http
      .get<ApiResponse<Page<OfferResponse>>>(`${this.apiUrl}/search`, { params })
      .pipe(map((res) => res.data!));
  }

  filterOffers(
    filters: { keyword?: string; location?: string; contractType?: OfferContractType },
    page = 0,
    size = 20,
  ): Observable<Page<OfferResponse>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (filters.keyword) params = params.set('keyword', filters.keyword);
    if (filters.location) params = params.set('location', filters.location);
    if (filters.contractType) params = params.set('contractType', filters.contractType);
    return this.http
      .get<ApiResponse<Page<OfferResponse>>>(`${this.apiUrl}/filter`, { params })
      .pipe(map((res) => res.data!));
  }
}

