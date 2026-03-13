import { Injectable, inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { map, exhaustMap, catchError } from 'rxjs/operators';
import { CompanyService } from '../../../core/services/company.service';
import { CompanyActions } from './company.actions';

@Injectable()
export class CompanyEffects {
  private readonly actions$ = inject(Actions);
  private readonly companyService = inject(CompanyService);

  loadCompany$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CompanyActions.loadCompany),
      exhaustMap(({ userId }) =>
        this.companyService.getCompanyByUserId(userId).pipe(
          map((company) => CompanyActions.loadCompanySuccess({ company })),
          catchError((err) => {
            if (err.status === 404) {
              return of(CompanyActions.loadCompanyNotFound());
            }
            return of(CompanyActions.loadCompanyFailure({
              error: err?.error?.message || 'Failed to load company',
            }));
          }),
        ),
      ),
    ),
  );

  createCompany$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CompanyActions.createCompany),
      exhaustMap(({ request }) =>
        this.companyService.createCompany(request).pipe(
          map((company) => CompanyActions.createCompanySuccess({ company })),
          catchError((err) =>
            of(CompanyActions.createCompanyFailure({
              error: err?.error?.message || 'Failed to create company',
            })),
          ),
        ),
      ),
    ),
  );

  updateCompany$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CompanyActions.updateCompany),
      exhaustMap(({ userId, request }) =>
        this.companyService.updateCompany(userId, request).pipe(
          map((company) => CompanyActions.updateCompanySuccess({ company })),
          catchError((err) =>
            of(CompanyActions.updateCompanyFailure({
              error: err?.error?.message || 'Failed to update company',
            })),
          ),
        ),
      ),
    ),
  );

  deleteCompany$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CompanyActions.deleteCompany),
      exhaustMap(({ userId }) =>
        this.companyService.deleteCompany(userId).pipe(
          map(() => CompanyActions.deleteCompanySuccess()),
          catchError((err) =>
            of(CompanyActions.deleteCompanyFailure({
              error: err?.error?.message || 'Failed to delete company',
            })),
          ),
        ),
      ),
    ),
  );

  loadPendingCompanies$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CompanyActions.loadPendingCompanies),
      exhaustMap(() =>
        this.companyService.getPendingCompanies().pipe(
          map((companies) => CompanyActions.loadPendingCompaniesSuccess({ companies })),
          catchError((err) =>
            of(CompanyActions.loadPendingCompaniesFailure({
              error: err?.error?.message || 'Failed to load pending companies',
            })),
          ),
        ),
      ),
    ),
  );

  validateCompany$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CompanyActions.validateCompany),
      exhaustMap(({ companyId, request }) =>
        this.companyService.validateCompany(companyId, request).pipe(
          map((company) => CompanyActions.validateCompanySuccess({ company })),
          catchError((err) =>
            of(CompanyActions.validateCompanyFailure({
              error: err?.error?.message || 'Failed to validate company',
            })),
          ),
        ),
      ),
    ),
  );
}

