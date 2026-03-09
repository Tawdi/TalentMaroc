import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Guard for routes that require authentication
 */
export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated()) {
    return true;
  }

  // Redirect to login with return URL
  router.navigate(['/auth/login'], {
    queryParams: { returnUrl: state.url },
  });
  return false;
};

/**
 * Guard for routes that should only be accessible to guests (not logged in)
 */
export const guestGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAuthenticated()) {
    return true;
  }

  // Redirect to dashboard
  router.navigate(['/dashboard']);
  return false;
};

/**
 * Guard for candidate-only routes
 */
export const candidateGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isCandidate()) {
    return true;
  }

  if (!authService.isAuthenticated()) {
    router.navigate(['/auth/login']);
    return false;
  }

  // User is logged in but not a candidate
  router.navigate(['/dashboard']);
  return false;
};

/**
 * Guard for company-only routes
 */
export const companyGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isCompany()) {
    return true;
  }

  if (!authService.isAuthenticated()) {
    router.navigate(['/auth/login']);
    return false;
  }

  router.navigate(['/dashboard']);
  return false;
};

/**
 * Guard for admin-only routes
 */
export const adminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAdmin()) {
    return true;
  }

  if (!authService.isAuthenticated()) {
    router.navigate(['/auth/login']);
    return false;
  }

  router.navigate(['/dashboard']);
  return false;
};

