import {Routes} from '@angular/router';
import {adminGuard, authGuard, guestGuard} from './core/guards/auth.guard';

export const routes: Routes = [
  // Public routes with Public Layout
  {
    path: '',
    loadComponent: () => import('./layouts/public-layout/public-layout').then(m => m.PublicLayoutComponent),
    children: [
      {
        path: '',
        loadComponent: () => import('./features/home/home').then(m => m.HomeComponent),
      },
      {
        path: 'jobs',
        loadComponent: () => import('./features/home/home').then(m => m.HomeComponent), // Placeholder
      },
      {
        path: 'companies',
        loadComponent: () => import('./features/home/home').then(m => m.HomeComponent), // Placeholder
      },
      {
        path: 'about',
        loadComponent: () => import('./features/home/home').then(m => m.HomeComponent), // Placeholder
      },
      {
        path: 'ui-demo',
        loadComponent: () => import('./features/ui-demo/ui-demo').then(m => m.UiDemoComponent),
      },
    ],
  },

  // Auth routes (no layout - full page) - only for guests
  {
    path: 'auth',
    canActivate: [guestGuard],
    children: [
      {
        path: 'login',
        loadComponent: () => import('./features/auth/login/login').then(m => m.LoginComponent),
      },
      {
        path: 'register',
        loadComponent: () => import('./features/auth/register/register').then(m => m.RegisterComponent),
      },
      {
        path: 'forgot-password',
        loadComponent: () => import('./features/auth/login/login').then(m => m.LoginComponent), // Placeholder
      },
      {
        path: 'verify-email',
        loadComponent: () => import('./features/auth/login/login').then(m => m.LoginComponent), // Placeholder
      },
      {
        path: '',
        redirectTo: 'login',
        pathMatch: 'full',
      },
    ],
  },

  // Dashboard routes with Dashboard Layout (for authenticated users)
  {
    path: 'dashboard',
    loadComponent: () => import('./layouts/dashboard-layout/dashboard-layout').then(m => m.DashboardLayoutComponent),
    canActivate: [authGuard],
    children: [
      {
        path: '',
        loadComponent: () => import('./features/dashboard/candidate-dashboard/candidate-dashboard').then(m => m.CandidateDashboardComponent),
      },
      {
        path: 'applications',
        loadComponent: () => import('./features/dashboard/candidate-dashboard/candidate-dashboard').then(m => m.CandidateDashboardComponent), // Placeholder
      },
      {
        path: 'saved-jobs',
        loadComponent: () => import('./features/dashboard/candidate-dashboard/candidate-dashboard').then(m => m.CandidateDashboardComponent), // Placeholder
      },
      {
        path: 'profile',
        loadComponent: () => import('./features/dashboard/candidate-dashboard/candidate-dashboard').then(m => m.CandidateDashboardComponent), // Placeholder
      },
      {
        path: 'settings',
        loadComponent: () => import('./features/dashboard/candidate-dashboard/candidate-dashboard').then(m => m.CandidateDashboardComponent), // Placeholder
      },
      {
        path: 'messages',
        loadComponent: () => import('./features/dashboard/candidate-dashboard/candidate-dashboard').then(m => m.CandidateDashboardComponent), // Placeholder
      },
    ],
  },

  // Admin routes with Dashboard Layout
  {
    path: 'admin',
    loadComponent: () => import('./layouts/dashboard-layout/dashboard-layout').then(m => m.DashboardLayoutComponent),
    canActivate: [authGuard, adminGuard],
    children: [
      {
        path: '',
        loadComponent: () => import('./features/dashboard/candidate-dashboard/candidate-dashboard').then(m => m.CandidateDashboardComponent), // Placeholder
      },
    ],
  },

  // Redirect unknown routes
  {
    path: '**',
    redirectTo: '',
  },
];
