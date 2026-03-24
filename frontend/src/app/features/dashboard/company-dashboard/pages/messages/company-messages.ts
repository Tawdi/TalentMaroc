import { Component, ChangeDetectionStrategy } from '@angular/core';
import { RouterLink } from '@angular/router';
import { UiCard, UiButton, IconComponent } from '../../../../../shared';

@Component({
  selector: 'app-company-messages',
  standalone: true,
  imports: [UiCard, UiButton, IconComponent, RouterLink],
  templateUrl: './company-messages.html',
  styleUrl: './company-messages.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CompanyMessagesComponent {}

