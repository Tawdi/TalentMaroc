import {Routes} from '@angular/router';
import {provideState} from '@ngrx/store';
import {provideEffects} from '@ngrx/effects';
import {adminGuard, authGuard, companyGuard, guestGuard} from './core/guards/auth.guard';
import {
  COMPANY_OFFERS_FEATURE_KEY,
  companyOffersReducers,
  CompanyEffects,
  OfferEffects,
} from './features/company-offers/store';
import {
  APPLICATIONS_FEATURE_KEY,
  applicationsReducers,
  ApplicationEffects,
} from './features/applications/store';
import { adminReducer, AdminEffects } from './features/dashboard/admin-dashboard/store';
import {
  LoginComponent,
  RegisterComponent,
  VerifyEmailComponent,
  ResendVerificationComponent,
  ForgotPasswordComponent,
  ResetPasswordComponent,
} from './features/auth';

const featureProviders = [
  provideState(COMPANY_OFFERS_FEATURE_KEY, companyOffersReducers),
  provideEffects(CompanyEffects, OfferEffects),
  provideState(APPLICATIONS_FEATURE_KEY, applicationsReducers),
  provideEffects(ApplicationEffects),
];

export const routes: Routes = [
  // Public routes with Public Layout
  {
    path: '',
    loadComponent: () => import('./layouts/public-layout/public-layout').then(m => m.PublicLayoutComponent),
    providers: [...featureProviders],
    children: [
      {
        path: '',
        loadComponent: () => import('./features/home/home').then(m => m.HomeComponent),
      },
      {
        path: 'jobs',
        loadComponent: () => import('./features/company-offers/pages/job-listings/job-listings').then(m => m.JobListingsComponent),
      },
      {
        path: 'jobs/:offerId',
        loadComponent: () => import('./features/company-offers/pages/job-detail/job-detail').then(m => m.JobDetailComponent),
      },
      {
        path: 'register/company',
        canActivate: [guestGuard],
        loadComponent: () => import('./features/company-offers/pages/company-register/public-company-register').then(m => m.PublicCompanyRegisterComponent),
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
        loadComponent: () => Promise.resolve(LoginComponent),
      },
      {
        path: 'register',
        loadComponent: () => Promise.resolve(RegisterComponent),
      },
      {
        path: 'forgot-password',
        loadComponent: () => Promise.resolve(ForgotPasswordComponent),
      },
      {
        path: 'verify-email',
        loadComponent: () => Promise.resolve(VerifyEmailComponent),
      },
      {
        path: 'resend-verification',
        loadComponent: () => Promise.resolve(ResendVerificationComponent),
      },
      {
        path: 'reset-password',
        loadComponent: () => Promise.resolve(ResetPasswordComponent),
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
    providers: [...featureProviders],
    children: [
      {
        path: '',
        loadComponent: () => import('./features/dashboard/dashboard-home/dashboard-home').then(m => m.DashboardHomeComponent),
      },
      {
        path: 'applications',
        loadComponent: () => import('./features/applications/pages/my-applications/my-applications').then(m => m.MyApplicationsComponent),
      },
      {
        path: 'saved-jobs',
        loadComponent: () => import('./features/dashboard/candidate-dashboard/pages/saved-jobs/saved-jobs').then(m => m.SavedJobsComponent),
      },
      {
        path: 'resume',
        loadComponent: () => import('./features/dashboard/candidate-dashboard/pages/resume-builder/resume-builder').then(m => m.ResumeBuilderComponent),
      },
      {
        path: 'alerts',
        loadComponent: () => import('./features/dashboard/candidate-dashboard/pages/job-alerts/job-alerts').then(m => m.JobAlertsComponent),
      },
      {
        path: 'profile',
        loadComponent: () => import('./features/dashboard/candidate-profile/candidate-profile').then(m => m.CandidateProfileComponent),
      },
      // Company routes
      {
        path: 'company-profile',
        canActivate: [companyGuard],
        loadComponent: () => import('./features/company-offers/pages/company-profile/company-profile').then(m => m.CompanyProfileComponent),
      },
      {
        path: 'company-register',
        canActivate: [companyGuard],
        loadComponent: () => import('./features/company-offers/pages/company-register/company-register').then(m => m.CompanyRegisterComponent),
      },
      {
        path: 'offers',
        canActivate: [companyGuard],
        loadComponent: () => import('./features/company-offers/pages/manage-offers/manage-offers').then(m => m.ManageOffersComponent),
      },
      {
        path: 'offers/:offerId/applications',
        canActivate: [companyGuard],
        loadComponent: () => import('./features/applications/pages/offer-applications/offer-applications').then(m => m.OfferApplicationsComponent),
      },
      {
        path: 'settings',
        loadComponent: () => import('./features/dashboard/candidate-dashboard/candidate-dashboard').then(m => m.CandidateDashboardComponent), // Placeholder
      },
      {
        path: 'messages',
        loadComponent: () => import('./features/dashboard/dashboard-messages/dashboard-messages').then(m => m.DashboardMessagesComponent),
      },
    ],
  },

  // Admin routes with Dashboard Layout
  {
    path: 'admin',
    loadComponent: () => import('./layouts/dashboard-layout/dashboard-layout').then(m => m.DashboardLayoutComponent),
    canActivate: [authGuard, adminGuard],
    providers: [
      ...featureProviders,
      provideState('admin', adminReducer),
      provideEffects(AdminEffects),
    ],
    children: [
      {
        path: '',
        loadComponent: () => import('./features/dashboard/admin-dashboard/admin-dashboard').then(m => m.AdminDashboardComponent),
      },
      {
        path: 'users',
        loadComponent: () => import('./features/dashboard/admin-dashboard/pages/users/admin-users').then(m => m.AdminUsersComponent),
      },
      {
        path: 'companies',
        loadComponent: () => import('./features/dashboard/admin-dashboard/pages/companies/admin-companies').then(m => m.AdminCompaniesComponent),
      },
      {
        path: 'jobs',
        loadComponent: () => import('./features/dashboard/admin-dashboard/pages/jobs/admin-jobs').then(m => m.AdminJobsComponent),
      },
      {
        path: 'applications',
        loadComponent: () => import('./features/dashboard/admin-dashboard/pages/applications/admin-applications').then(m => m.AdminApplicationsComponent),
      },
    ],
  },

  // Redirect unknown routes
  {
    path: '**',
    redirectTo: '',
  },
];
