import { createActionGroup, emptyProps, props } from '@ngrx/store';
import {
  CompanyResponse,
  CompanySummaryResponse,
  CreateCompanyRequest,
  UpdateCompanyRequest,
  ValidateCompanyRequest,
} from '../../../core/models/company-offers.model';

export const CompanyActions = createActionGroup({
  source: 'Company',
  events: {
    // Load company by userId
    'Load Company': props<{ userId: string }>(),
    'Load Company Success': props<{ company: CompanyResponse }>(),
    'Load Company Failure': props<{ error: string }>(),
    'Load Company Not Found': emptyProps(),

    // Create company
    'Create Company': props<{ request: CreateCompanyRequest }>(),
    'Create Company Success': props<{ company: CompanyResponse }>(),
    'Create Company Failure': props<{ error: string }>(),

    // Update company
    'Update Company': props<{ userId: string; request: UpdateCompanyRequest }>(),
    'Update Company Success': props<{ company: CompanyResponse }>(),
    'Update Company Failure': props<{ error: string }>(),

    // Delete company
    'Delete Company': props<{ userId: string }>(),
    'Delete Company Success': emptyProps(),
    'Delete Company Failure': props<{ error: string }>(),

    // Admin: load pending companies
    'Load Pending Companies': emptyProps(),
    'Load Pending Companies Success': props<{ companies: CompanySummaryResponse[] }>(),
    'Load Pending Companies Failure': props<{ error: string }>(),

    // Admin: validate company
    'Validate Company': props<{ companyId: number; request: ValidateCompanyRequest }>(),
    'Validate Company Success': props<{ company: CompanyResponse }>(),
    'Validate Company Failure': props<{ error: string }>(),

    // Clear errors
    'Clear Error': emptyProps(),
  },
});

