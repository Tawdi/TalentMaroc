import { Component, ChangeDetectionStrategy, inject, computed } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';
import { MessagesComponent as CandidateMessagesComponent } from '../candidate-dashboard/pages/messages/messages';
import { CompanyMessagesComponent } from '../company-dashboard/pages/messages/company-messages';

@Component({
  selector: 'app-dashboard-messages',
  standalone: true,
  imports: [CandidateMessagesComponent, CompanyMessagesComponent],
  templateUrl: './dashboard-messages.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardMessagesComponent {
  private readonly authService = inject(AuthService);
  readonly role = computed(() => this.authService.currentUser()?.role || 'CANDIDATE');
}
