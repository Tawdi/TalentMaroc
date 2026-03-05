import { Component, signal, computed, inject, ChangeDetectionStrategy } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { ThemeToggleComponent } from '../../shared/components/theme-toggle.component';
import { AuthService } from '../../core/services/auth.service';
import { UserRole } from '../../core/models/auth.model';

export interface NavItem {
  label: string;
  icon: string;
  route: string;
  badge?: number;
}

@Component({
  selector: 'app-dashboard-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, ThemeToggleComponent],
  templateUrl: './dashboard-layout.html',
  styleUrl: './dashboard-layout.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardLayoutComponent {
  private readonly authService = inject(AuthService);

  // State
  protected readonly isSidebarOpen = signal(true);
  protected readonly isMobileSidebarOpen = signal(false);
  protected readonly isProfileDropdownOpen = signal(false);

  // User data from auth service
  protected readonly currentUser = this.authService.currentUser;

  // Computed user info
  protected readonly userName = computed(() => {
    const user = this.currentUser();
    return user?.name || user?.username || 'User';
  });

  protected readonly userEmail = computed(() => this.currentUser()?.email || '');

  protected readonly userRole = computed(() => this.currentUser()?.role || 'CANDIDATE');

  // Avatar placeholder - in a real app, this would come from a profile service
  protected readonly userAvatar = computed(() => '');

  // Computed navigation based on role
  protected readonly navItems = computed<NavItem[]>(() => {
    const role = this.userRole();

    switch (role) {
      case 'CANDIDATE':
        return [
          { label: 'Dashboard', icon: 'dashboard', route: '/dashboard' },
          { label: 'My Applications', icon: 'applications', route: '/dashboard/applications', badge: 3 },
          { label: 'Saved Jobs', icon: 'bookmark', route: '/dashboard/saved-jobs', badge: 12 },
          { label: 'My Profile', icon: 'profile', route: '/dashboard/profile' },
          { label: 'Resume Builder', icon: 'resume', route: '/dashboard/resume' },
          { label: 'Job Alerts', icon: 'bell', route: '/dashboard/alerts' },
          { label: 'Messages', icon: 'message', route: '/dashboard/messages', badge: 5 },
          { label: 'Settings', icon: 'settings', route: '/dashboard/settings' },
        ];

      case 'COMPANY':
        return [
          { label: 'Dashboard', icon: 'dashboard', route: '/dashboard' },
          { label: 'Post a Job', icon: 'plus', route: '/dashboard/post-job' },
          { label: 'My Job Listings', icon: 'briefcase', route: '/dashboard/jobs' },
          { label: 'Applications', icon: 'applications', route: '/dashboard/applications', badge: 24 },
          { label: 'Candidates', icon: 'users', route: '/dashboard/candidates' },
          { label: 'Company Profile', icon: 'building', route: '/dashboard/company-profile' },
          { label: 'Messages', icon: 'message', route: '/dashboard/messages', badge: 8 },
          { label: 'Analytics', icon: 'chart', route: '/dashboard/analytics' },
          { label: 'Settings', icon: 'settings', route: '/dashboard/settings' },
        ];

      case 'ADMIN':
        return [
          { label: 'Dashboard', icon: 'dashboard', route: '/admin' },
          { label: 'Users', icon: 'users', route: '/admin/users' },
          { label: 'Companies', icon: 'building', route: '/admin/companies' },
          { label: 'Job Listings', icon: 'briefcase', route: '/admin/jobs' },
          { label: 'Applications', icon: 'applications', route: '/admin/applications' },
          { label: 'Reports', icon: 'chart', route: '/admin/reports' },
          { label: 'Moderation', icon: 'shield', route: '/admin/moderation', badge: 7 },
          { label: 'Settings', icon: 'settings', route: '/admin/settings' },
        ];

      default:
        return [];
    }
  });

  // Role display name
  protected readonly roleDisplayName = computed(() => {
    const role = this.userRole();
    const names: Record<UserRole, string> = {
      CANDIDATE: 'Job Seeker',
      COMPANY: 'Employer',
      ADMIN: 'Administrator',
    };
    return names[role] || 'User';
  });

  // Get initials for avatar fallback
  protected readonly userInitials = computed(() => {
    const name = this.userName();
    const parts = name.split(' ');
    if (parts.length >= 2) {
      return (parts[0][0] + parts[1][0]).toUpperCase();
    }
    return name.substring(0, 2).toUpperCase();
  });

  // Methods
  toggleSidebar(): void {
    this.isSidebarOpen.update(v => !v);
  }

  toggleMobileSidebar(): void {
    this.isMobileSidebarOpen.update(v => !v);
  }

  closeMobileSidebar(): void {
    this.isMobileSidebarOpen.set(false);
  }

  toggleProfileDropdown(): void {
    this.isProfileDropdownOpen.update(v => !v);
  }

  closeProfileDropdown(): void {
    this.isProfileDropdownOpen.set(false);
  }

  logout(): void {
    this.authService.logout().subscribe();
  }
}


