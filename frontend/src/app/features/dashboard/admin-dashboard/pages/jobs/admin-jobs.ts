import { Component, ChangeDetectionStrategy } from '@angular/core';
import { RouterLink } from '@angular/router';
import { UiCard, UiButton, IconComponent } from '../../../../../shared';

@Component({
  selector: 'app-admin-jobs',
  standalone: true,
  imports: [UiCard, UiButton, IconComponent, RouterLink],
  templateUrl: './admin-jobs.html',
  styleUrl: './admin-jobs.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminJobsComponent {}

