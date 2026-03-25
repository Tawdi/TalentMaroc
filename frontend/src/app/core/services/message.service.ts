import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/auth.model';
import { ApplicationMessage } from '../models/message.model';

export interface CreateMessageRequest {
  applicationId: number;
  senderUserId: string;
  content: string;
}

@Injectable({ providedIn: 'root' })
export class MessageService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/applications/api/v1/applications`;

  getMessages(applicationId: number): Observable<ApplicationMessage[]> {
    return this.http
      .get<ApiResponse<ApplicationMessage[]>>(`${this.apiUrl}/${applicationId}/messages`)
      .pipe(map((res) => res.data || []));
  }

  sendMessage(request: CreateMessageRequest): Observable<ApplicationMessage> {
    return this.http
      .post<ApiResponse<ApplicationMessage>>(`${this.apiUrl}/${request.applicationId}/messages`, request)
      .pipe(map((res) => res.data!));
  }
}

