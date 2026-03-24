import { Component, ChangeDetectionStrategy, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { UiCard, UiButton, IconComponent } from '../../../../../shared';
import { AuthService } from '../../../../../core/services/auth.service';
import { MessageService } from '../../../../../core/services/message.service';

@Component({
  selector: 'app-messages',
  standalone: true,
  imports: [UiCard, UiButton, IconComponent, RouterLink],
  templateUrl: './messages.html',
  styleUrl: './messages.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MessagesComponent {
  private readonly authService = inject(AuthService);
  private readonly messageService = inject(MessageService);

}
