import { inject, Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { AdminActions } from './admin.actions';
import { AuthService } from '../../../../core/services/auth.service';
import { CompanyService } from '../../../../core/services/company.service';
import { catchError, map, mergeMap, of } from 'rxjs';

@Injectable()
export class AdminEffects {
  private actions$ = inject(Actions);
  private authService = inject(AuthService);
  private companyService = inject(CompanyService);

  loadUsers$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AdminActions.loadUsers),
      mergeMap(({ page }) =>
        this.authService.getAllUsers(page).pipe(
          map(response => AdminActions.loadUsersSuccess({ response })),
          catchError((error) => of(AdminActions.loadUsersFailure({ error: error.message })))
        )
      )
    )
  );

  deleteUser$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AdminActions.deleteUser),
      mergeMap(({ userId }) =>
        this.authService.deleteUser(userId).pipe(
          map(() => AdminActions.deleteUserSuccess({ userId })),
          catchError((error) => of(AdminActions.deleteUserFailure({ error: error.message })))
        )
      )
    )
  );

  loadCompanies$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AdminActions.loadCompanies),
      mergeMap(({ page }) =>
        this.companyService.getAllCompanies(page).pipe(
          map(response => AdminActions.loadCompaniesSuccess({ response })),
          catchError((error) => of(AdminActions.loadCompaniesFailure({ error: error.message })))
        )
      )
    )
  );

  deleteCompany$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AdminActions.deleteCompany),
      mergeMap(({ companyId }) =>
        this.companyService.deleteCompanyById(companyId).pipe(
          map(() => AdminActions.deleteCompanySuccess({ companyId })),
          catchError((error) => of(AdminActions.deleteCompanyFailure({ error: error.message })))
        )
      )
    )
  );

  validateCompany$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AdminActions.validateCompany),
      mergeMap(({ companyId, request }) =>
        this.companyService.validateCompany(companyId, request).pipe(
          map(company => AdminActions.validateCompanySuccess({ company })),
          catchError((error) => of(AdminActions.validateCompanyFailure({ error: error.message })))
        )
      )
    )
  );
}
