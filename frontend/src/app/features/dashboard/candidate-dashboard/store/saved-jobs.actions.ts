import { createActionGroup, emptyProps, props } from '@ngrx/store';
import { SavedJob } from '../../../../core/models/saved-jobs.model';
import { OfferResponse } from '../../../../core/models/company-offers.model';

export const SavedJobsActions = createActionGroup({
  source: 'Saved Jobs',
  events: {
    'Load Saved Jobs': props<{ userId: string }>(),
    'Load Saved Jobs Success': props<{ savedJobs: SavedJob[] }>(),
    'Load Saved Jobs Failure': props<{ error: string }>(),

    'Save Job': props<{ userId: string; offer: OfferResponse }>(),
    'Save Job Success': props<{ savedJob: SavedJob }>(),
    'Save Job Failure': props<{ offerId: number; error: string }>(),

    'Remove Job': props<{ userId: string; offerId: number }>(),
    'Remove Job Success': props<{ offerId: number }>(),
    'Remove Job Failure': props<{ offerId: number; error: string }>(),

    'Clear Saved Jobs Error': emptyProps(),
    'Reset Saved Jobs': emptyProps(),
  },
});

