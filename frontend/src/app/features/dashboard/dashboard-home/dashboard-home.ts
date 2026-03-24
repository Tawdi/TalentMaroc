import { Component, ChangeDetectionStrategy, inject, computed } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';
import { CandidateDashboardComponent } from '../candidate-dashboard/candidate-dashboard';
import { CompanyDashboardComponent } from '../company-dashboard/company-dashboard';
import {AdminDashboardComponent} from '../admin-dashboard/admin-dashboard';

@Component({
  selector: 'app-dashboard-home',
  standalone: true,
  imports: [CandidateDashboardComponent, CompanyDashboardComponent, AdminDashboardComponent],
  templateUrl: './dashboard-home.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardHomeComponent {
  private readonly authService = inject(AuthService);
  readonly role = computed(() => this.authService.currentUser()?.role || 'CANDIDATE');
}
