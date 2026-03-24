import { Component, ChangeDetectionStrategy, OnInit, inject, computed } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DecimalPipe } from '@angular/common';
import { Store } from '@ngrx/store';
import { UiCard, UiButton, UiAlert, IconComponent, Skeleton } from '../../../shared';
import {
  AdminActions,
  selectUsersTotalElements,
  selectCompaniesTotalElements,
  selectUsersLoading,
  selectCompaniesLoading,
  selectUsersError,
  selectCompaniesError,
} from './store';
import {
  CompanyActions,
  selectPendingCompanies,
  selectPendingLoading,
  selectPendingError,
} from '../../company-offers/store';
import {
  AdminPendingCompaniesComponent
} from '../../company-offers/pages/admin-pending-companies/admin-pending-companies';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [UiCard, UiButton, UiAlert, IconComponent, Skeleton, RouterLink, DecimalPipe, AdminPendingCompaniesComponent],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminDashboardComponent implements OnInit {
  private readonly store = inject(Store);

  readonly totalUsers = this.store.selectSignal(selectUsersTotalElements);
  readonly totalCompanies = this.store.selectSignal(selectCompaniesTotalElements);
  readonly pendingCompaniesList = this.store.selectSignal(selectPendingCompanies);

  readonly usersLoading = this.store.selectSignal(selectUsersLoading);
  readonly companiesLoading = this.store.selectSignal(selectCompaniesLoading);
  readonly pendingLoading = this.store.selectSignal(selectPendingLoading);

  readonly usersError = this.store.selectSignal(selectUsersError);
  readonly companiesError = this.store.selectSignal(selectCompaniesError);
  readonly pendingError = this.store.selectSignal(selectPendingError);

  readonly pendingCompanies = computed(() => this.pendingCompaniesList().length);
  readonly loading = computed(() => this.usersLoading() || this.companiesLoading() || this.pendingLoading());
  readonly error = computed(() => this.usersError() || this.companiesError() || this.pendingError());

  ngOnInit(): void {
    this.store.dispatch(AdminActions.loadUsers({ page: 0 }));
    this.store.dispatch(AdminActions.loadCompanies({ page: 0 }));
    this.store.dispatch(CompanyActions.loadPendingCompanies());
  }
}
