import { createActionGroup, emptyProps, props } from '@ngrx/store';
import {
  ApplicationResponse,
  ApplicationStatus,
  CreateApplicationRequest,
  UpdateApplicationStatusRequest,
} from '../../../core/models/application.model';
import { Page } from '../../../core/models/company-offers.model';

export const ApplicationActions = createActionGroup({
  source: 'Application',
  events: {
    // ======================== CANDIDATE ========================
    // Apply
    'Apply': props<{ userId: string; request: CreateApplicationRequest }>(),
    'Apply Success': props<{ application: ApplicationResponse; offerId: number }>(),
    'Apply Failure': props<{ error: string }>(),

    'Check Applied': props<{ userId: string; offerId: number }>(),
    'Check Applied Success': props<{ offerId: number; hasApplied: boolean }>(),
    'Check Applied Failure': props<{ error: string }>(),

    // My Applications
    'Load My Applications': props<{ userId: string; page?: number; size?: number }>(),
    'Load My Applications Success': props<{ applicationsPage: Page<ApplicationResponse> }>(),
    'Load My Applications Failure': props<{ error: string }>(),

    // Withdraw
    'Withdraw Application': props<{ userId: string; applicationId: number }>(),
    'Withdraw Application Success': props<{ application: ApplicationResponse }>(),
    'Withdraw Application Failure': props<{ error: string }>(),

    // ======================== COMPANY ========================
    // Load applications for an offer
    'Load Offer Applications': props<{
      userId: string;
      offerId: number;
      status?: ApplicationStatus;
      page?: number;
      size?: number;
    }>(),
    'Load Offer Applications Success': props<{ applicationsPage: Page<ApplicationResponse> }>(),
    'Load Offer Applications Failure': props<{ error: string }>(),

    // Update status
    'Update Application Status': props<{
      userId: string;
      applicationId: number;
      request: UpdateApplicationStatusRequest;
    }>(),
    'Update Application Status Success': props<{ application: ApplicationResponse }>(),
    'Update Application Status Failure': props<{ error: string }>(),

    // ======================== CLEAR ========================
    'Clear Error': emptyProps(),
    'Clear Apply Success': emptyProps(),
  },
});
