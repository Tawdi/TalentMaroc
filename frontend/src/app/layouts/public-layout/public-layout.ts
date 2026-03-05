import { Component, ChangeDetectionStrategy, inject, computed } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { ThemeToggleComponent } from '../../shared/components/theme-toggle.component';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-public-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, ThemeToggleComponent],
  templateUrl: './public-layout.html',
  styleUrl: './public-layout.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PublicLayoutComponent {
  protected readonly authService = inject(AuthService);

  protected readonly isAuthenticated = this.authService.isAuthenticated;
  protected readonly currentUser = this.authService.currentUser;

  protected readonly userName = computed(() => {
    const user = this.currentUser();
    return user?.name || user?.username || 'User';
  });

  logout(): void {
    this.authService.logout().subscribe();
  }
}

