import { CommonModule } from '@angular/common';
import { Component, ChangeDetectionStrategy, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { UiButton } from '../../../shared';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-oauth2-redirect',
  standalone: true,
  imports: [CommonModule, UiButton],
  templateUrl: './oauth2-redirect.html',
  styleUrl: './oauth2-redirect.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OAuth2RedirectComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);

  readonly isProcessing = signal(true);
  readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    const params = this.route.snapshot.queryParamMap;
    const query = {
      token: params.get('token'),
      refreshToken: params.get('refreshToken'),
      userId: params.get('userId'),
    };

    if (!query.token || !query.refreshToken || !query.userId) {
      this.handleError('Missing authentication details. Please try signing in again.');
      return;
    }

    this.authService.completeOAuthLogin(query).subscribe({
      next: () => {
        const queryReturnUrl = params.get('returnUrl');
        const destination = queryReturnUrl || this.authService.consumeOAuthReturnUrl('/dashboard');
        this.router.navigateByUrl(destination);
      },
      error: (err) => {
        this.handleError(err.message || 'OAuth sign-in failed. Please try again.');
      },
    });
  }

  retry(): void {
    this.router.navigate(['/auth/login']);
  }

  private handleError(message: string): void {
    this.isProcessing.set(false);
    this.errorMessage.set(message);
  }
}

