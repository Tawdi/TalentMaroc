import { Component, ChangeDetectionStrategy } from '@angular/core';
import { RouterLink } from '@angular/router';
import { UiCard, UiButton, IconComponent } from '../../../../../shared';

@Component({
  selector: 'app-admin-applications',
  standalone: true,
  imports: [UiCard, UiButton, IconComponent, RouterLink],
  templateUrl: './admin-applications.html',
  styleUrl: './admin-applications.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminApplicationsComponent {}

