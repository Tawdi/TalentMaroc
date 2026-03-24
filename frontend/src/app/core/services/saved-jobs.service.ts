import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/auth.model';
import { SavedJob } from '../models/saved-jobs.model';

@Injectable({ providedIn: 'root' })
export class SavedJobsService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/candidate-profile/api/v1/profiles`;

  getSavedJobs(userId: string): Observable<SavedJob[]> {
    return this.http
      .get<ApiResponse<SavedJob[]>>(`${this.apiUrl}/user/${userId}/saved-jobs`)
      .pipe(map((res) => res.data || []));
  }

  saveJob(userId: string, offerId: number): Observable<SavedJob> {
    return this.http
      .post<ApiResponse<SavedJob>>(`${this.apiUrl}/user/${userId}/saved-jobs`, null, {
        params: { offerId },
      })
      .pipe(map((res) => res.data!));
  }

  removeJob(userId: string, offerId: number): Observable<void> {
    return this.http
      .delete<ApiResponse<void>>(`${this.apiUrl}/user/${userId}/saved-jobs`, {
        params: { offerId },
      })
      .pipe(map(() => void 0));
  }
}

