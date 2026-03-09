import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req: HttpRequest<unknown>, next: HttpHandlerFn) => {
  const authService = inject(AuthService);

  // Skip auth header for auth endpoints (except logout and refresh)
  const isAuthEndpoint = req.url.includes('/api/auth/') &&
    !req.url.includes('/api/auth/logout') &&
    !req.url.includes('/api/auth/refresh-token');

  if (isAuthEndpoint) {
    return next(req);
  }

  const accessToken = authService.getAccessToken();

  if (!accessToken) {
    return next(req);
  }

  // Clone request with auth header
  const authReq = req.clone({
    setHeaders: {
      Authorization: `Bearer ${accessToken}`,
    },
  });

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      // If 401 and not a refresh request, try to refresh token
      if (error.status === 401 && !req.url.includes('/api/auth/refresh-token')) {
        return authService.refreshToken().pipe(
          switchMap(() => {
            // Retry with new token
            const newToken = authService.getAccessToken();
            const retryReq = req.clone({
              setHeaders: {
                Authorization: `Bearer ${newToken}`,
              },
            });
            return next(retryReq);
          }),
          catchError((refreshError) => {
            // Refresh failed, error will be handled by auth service
            return throwError(() => refreshError);
          })
        );
      }

      return throwError(() => error);
    })
  );
};

