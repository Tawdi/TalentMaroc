import { createActionGroup, emptyProps, props } from '@ngrx/store';
import {
  CompanyResponse,
  CompanySummaryResponse,
  ValidateCompanyRequest,
  Page,
} from '../../../../core/models/company-offers.model';
import { UserResponse } from '../../../../core/models/auth.model';

export const AdminActions = createActionGroup({
  source: 'Company Admin',
  events: {
    // Users
    'Load Users': props<{ page: number }>(),
    'Load Users Success': props<{ response: Page<UserResponse> }>(),
    'Load Users Failure': props<{ error: string }>(),

    'Delete User': props<{ userId: string }>(),
    'Delete User Success': props<{ userId: string }>(),
    'Delete User Failure': props<{ error: string }>(),

    // Companies
    'Load Companies': props<{ page: number }>(),
    'Load Companies Success': props<{ response: Page<CompanySummaryResponse> }>(),
    'Load Companies Failure': props<{ error: string }>(),

    'Delete Company': props<{ companyId: number }>(),
    'Delete Company Success': props<{ companyId: number }>(),
    'Delete Company Failure': props<{ error: string }>(),

    'Validate Company': props<{ companyId: number; request: ValidateCompanyRequest }>(),
    'Validate Company Success': props<{ company: CompanyResponse }>(),
    'Validate Company Failure': props<{ error: string }>(),

    // Clear errors
    'Clear Error': emptyProps(),
  },
});
