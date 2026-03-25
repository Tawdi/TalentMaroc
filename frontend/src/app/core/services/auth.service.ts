import { Injectable, signal, computed } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { tap, catchError, map } from 'rxjs/operators';
import {
  ApiResponse,
  RegisterRequest,
  LoginRequest,
  LoginResponse,
  UserResponse,
  TokenResponse,
  CurrentUser,
  UserRole,
} from '../models/auth.model';
import { Page } from '../models/company-offers.model';
import { environment } from '../../../environments/environment';

const AUTH_STORAGE_KEY = 'talent_maroc_auth';
const TOKEN_REFRESH_THRESHOLD = 5 * 60 * 1000; // Refresh 5 minutes before expiry
const OAUTH_RETURN_URL_KEY = 'talent_maroc_oauth_return_url';
type OAuthProvider = 'google' | 'github';
interface OAuthCallbackParams {
  token: string | null;
  refreshToken: string | null;
  userId: string | null;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly apiUrl = `${environment.apiUrl}/auth/api/auth`;

  // Reactive state using signals
  private readonly _currentUser = signal<CurrentUser | null>(this.loadUserFromStorage());
  private readonly _isLoading = signal(false);

  // Public readonly signals
  readonly currentUser = this._currentUser.asReadonly();
  readonly isLoading = this._isLoading.asReadonly();

  // Computed values
  readonly isAuthenticated = computed(() => !!this._currentUser());
  readonly userRole = computed(() => this._currentUser()?.role ?? null);
  readonly isCandidate = computed(() => this._currentUser()?.role === 'CANDIDATE');
  readonly isCompany = computed(() => this._currentUser()?.role === 'COMPANY');
  readonly isAdmin = computed(() => this._currentUser()?.role === 'ADMIN');

  // For token refresh mechanism
  private refreshTokenTimeout?: ReturnType<typeof setTimeout>;

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    // Start token refresh timer if user is already logged in
    if (this._currentUser()) {
      this.startRefreshTokenTimer();
    }
  }

  /**
   * Register a new candidate user
   */
  register(request: RegisterRequest): Observable<ApiResponse<UserResponse>> {
    this._isLoading.set(true);

    // Ensure role is CANDIDATE for public registration
    const payload: RegisterRequest = {
      ...request,
      roleName: 'CANDIDATE',
    };

    return this.http.post<ApiResponse<UserResponse>>(`${this.apiUrl}/register`, payload).pipe(
      tap(() => this._isLoading.set(false)),
      catchError((error) => {
        this._isLoading.set(false);
        return this.handleError(error);
      })
    );
  }

  /**
   * Register a new Company
   */
  registerCompany(request: RegisterRequest): Observable<ApiResponse<UserResponse>> {
    this._isLoading.set(true);

    const payload: RegisterRequest = {
      ...request,
      roleName: 'COMPANY',
    };

    return this.http.post<ApiResponse<UserResponse>>(`${this.apiUrl}/register`, payload).pipe(
      tap(() => this._isLoading.set(false)),
      catchError((error) => {
        this._isLoading.set(false);
        return this.handleError(error);
      })
    );
  }

  /**
   * Login with email/username and password
   */
  login(request: LoginRequest): Observable<ApiResponse<LoginResponse>> {
    this._isLoading.set(true);

    return this.http.post<ApiResponse<LoginResponse>>(`${this.apiUrl}/login`, request).pipe(
      tap((response) => {
        this._isLoading.set(false);
        if (response.status === 'SUCCESS' && response.data) {
          this.setSession(response.data);
        }
      }),
      catchError((error) => {
        this._isLoading.set(false);
        return this.handleError(error);
      })
    );
  }

  /**
   * Logout user
   */
  logout(): Observable<ApiResponse<string>> {
    const refreshToken = this._currentUser()?.refreshToken;

    // Clear local state immediately
    this.clearSession();

    if (!refreshToken) {
      this.router.navigate(['/auth/login']);
      return new Observable((observer) => {
        observer.next({ status: 'SUCCESS', message: 'Logged out', data: null, timestamp: Date.now() });
        observer.complete();
      });
    }

    return this.http.post<ApiResponse<string>>(`${this.apiUrl}/logout`, null, {
      params: { refreshToken }
    }).pipe(
      tap(() => {
        this.router.navigate(['/auth/login']);
      }),
      catchError((error) => {
        // Even if logout fails on server, user is logged out locally
        this.router.navigate(['/auth/login']);
        return this.handleError(error);
      })
    );
  }

  /**
   * Verify email with token
   */
  verifyEmail(token: string): Observable<ApiResponse<string>> {
    return this.http.get<ApiResponse<string>>(`${this.apiUrl}/verify-email`, {
      params: { token }
    }).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Resend verification email
   */
  resendVerificationEmail(email: string): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>(`${this.apiUrl}/resend-verification`, null, {
      params: { email }
    }).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Request password reset
   */
  forgotPassword(email: string): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>(`${this.apiUrl}/forgot-password`, { email }).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Reset password with token
   */
  resetPassword(token: string, newPassword: string): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>(`${this.apiUrl}/reset-password`, {
      token,
      newPassword
    }).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Refresh access token
   */
  refreshToken(): Observable<ApiResponse<TokenResponse>> {
    const refreshToken = this._currentUser()?.refreshToken;

    if (!refreshToken) {
      return throwError(() => new Error('No refresh token available'));
    }

    return this.http.post<ApiResponse<TokenResponse>>(`${this.apiUrl}/refresh-token`, {
      refreshToken
    }).pipe(
      tap((response) => {
        if (response.status === 'SUCCESS' && response.data) {
          this.updateTokens(response.data);
        }
      }),
      catchError((error) => {
        // If refresh fails, logout user
        this.clearSession();
        this.router.navigate(['/auth/login']);
        return this.handleError(error);
      })
    );
  }

  /**
   * Get current access token (for interceptor)
   */
  getAccessToken(): string | null {
    return this._currentUser()?.accessToken ?? null;
  }

  /**
   * Check if token is about to expire
   */
  isTokenExpiringSoon(): boolean {
    const user = this._currentUser();
    if (!user) return false;

    const now = Date.now();
    return user.expiresAt - now < TOKEN_REFRESH_THRESHOLD;
  }

  /**
   * Start OAuth login process
   */
  startOAuthLogin(provider: OAuthProvider, returnUrl?: string): void {
    const targetReturnUrl = returnUrl ?? this.router.url ?? '/dashboard';
    try {
      sessionStorage.setItem(OAUTH_RETURN_URL_KEY, targetReturnUrl);
    } catch (e) {
      console.warn('Unable to persist OAuth return URL', e);
    }

    const oauthUrl = this.buildOAuthAuthorizeUrl(provider);
    window.location.href = oauthUrl;
  }

  /**
   * Complete OAuth login process
   */
  completeOAuthLogin(params: OAuthCallbackParams): Observable<void> {
    const { token, refreshToken, userId } = params;
    if (!token || !refreshToken || !userId) {
      return throwError(() => new Error('Missing OAuth credentials. Please try signing in again.'));
    }

    const headers = { Authorization: `Bearer ${token}` };
    return this.http
      .get<ApiResponse<UserResponse>>(`${environment.apiUrl}/auth/api/users/${userId}`, { headers })
      .pipe(
        map((res) => res.data!),
        tap((user) => {
          const currentUser: CurrentUser = {
            id: user.id,
            email: user.email,
            username: user.username,
            name: user.name,
            role: user.role as UserRole,
            accessToken: token,
            refreshToken,
            expiresAt: Date.now() + environment.oauthTokenExpirationMs,
          };
          this.persistCurrentUser(currentUser);
        }),
        map(() => void 0),
        catchError((error) => this.handleError(error))
      );
  }

  /**
   * Consume the stored OAuth return URL, if available
   */
  consumeOAuthReturnUrl(defaultUrl: string = '/dashboard'): string {
    try {
      const url = sessionStorage.getItem(OAUTH_RETURN_URL_KEY);
      if (url) {
        sessionStorage.removeItem(OAUTH_RETURN_URL_KEY);
        return url;
      }
    } catch (e) {
      console.warn('Unable to read OAuth return URL', e);
    }
    return defaultUrl;
  }

  // ============================================
  // PRIVATE METHODS
  // ============================================

  private buildOAuthAuthorizeUrl(provider: OAuthProvider): string {
    const redirectUri = encodeURIComponent(environment.oauthRedirectUri);
    return `${environment.apiUrl}/auth/oauth2/authorize/${provider}?redirect_uri=${redirectUri}`;
  }

  private persistCurrentUser(user: CurrentUser): void {
    this._currentUser.set(user);
    this.saveUserToStorage(user);
    this.startRefreshTokenTimer();
  }

  private setSession(loginResponse: LoginResponse): void {
    const expiresAt = Date.now() + (loginResponse.expiresIn * 1000);

    const user: CurrentUser = {
      id: loginResponse.userId,
      email: loginResponse.email,
      username: loginResponse.username,
      role: loginResponse.role as UserRole,
      accessToken: loginResponse.accessToken,
      refreshToken: loginResponse.refreshToken,
      expiresAt,
    };

    this.persistCurrentUser(user);
  }

  private updateTokens(tokenResponse: TokenResponse): void {
    const currentUser = this._currentUser();
    if (!currentUser) return;

    const expiresAt = Date.now() + (tokenResponse.expiresIn * 1000);

    const updatedUser: CurrentUser = {
      ...currentUser,
      accessToken: tokenResponse.accessToken,
      refreshToken: tokenResponse.refreshToken,
      expiresAt,
    };

    this.persistCurrentUser(updatedUser);
  }

  private clearSession(): void {
    this._currentUser.set(null);
    this.removeUserFromStorage();
    this.stopRefreshTokenTimer();
  }

  private startRefreshTokenTimer(): void {
    this.stopRefreshTokenTimer();

    const user = this._currentUser();
    if (!user) return;

    const timeout = user.expiresAt - Date.now() - TOKEN_REFRESH_THRESHOLD;

    if (timeout > 0) {
      this.refreshTokenTimeout = setTimeout(() => {
        this.refreshToken().subscribe();
      }, timeout);
    }
  }

  private stopRefreshTokenTimer(): void {
    if (this.refreshTokenTimeout) {
      clearTimeout(this.refreshTokenTimeout);
    }
  }

  // Storage methods
  private saveUserToStorage(user: CurrentUser): void {
    try {
      localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(user));
    } catch (e) {
      console.error('Failed to save user to storage', e);
    }
  }

  private loadUserFromStorage(): CurrentUser | null {
    try {
      const stored = localStorage.getItem(AUTH_STORAGE_KEY);
      if (!stored) return null;

      const user: CurrentUser = JSON.parse(stored);

      // Check if token is expired
      if (user.expiresAt < Date.now()) {
        this.removeUserFromStorage();
        return null;
      }

      return user;
    } catch (e) {
      console.error('Failed to load user from storage', e);
      return null;
    }
  }

  private removeUserFromStorage(): void {
    try {
      localStorage.removeItem(AUTH_STORAGE_KEY);
    } catch (e) {
      console.error('Failed to remove user from storage', e);
    }
  }

  // Error handling
  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An unexpected error occurred';

    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = error.error.message;
    } else if (error.error && typeof error.error === 'object') {
      // Server-side error with ApiResponse structure
      const apiError = error.error as ApiResponse<unknown>;
      errorMessage = apiError.message || errorMessage;

      // If there are validation errors, format them
      if (apiError.errors) {
        if (Array.isArray(apiError.errors)) {
          errorMessage = apiError.errors.join(', ');
        } else if (typeof apiError.errors === 'object') {
          errorMessage = Object.values(apiError.errors).join(', ');
        }
      }
    } else if (error.status === 0) {
      errorMessage = 'Unable to connect to server. Please check your connection.';
    } else if (error.status === 401) {
      errorMessage = 'Invalid credentials';
    } else if (error.status === 403) {
      errorMessage = 'Access denied';
    } else if (error.status === 404) {
      errorMessage = 'Resource not found';
    } else if (error.status >= 500) {
      errorMessage = 'Server error. Please try again later.';
    }

    return throwError(() => new Error(errorMessage));
  }

  // Admin methods
  getAllUsers(page: number = 0, size: number = 20): Observable<Page<UserResponse>> {
    return this.http.get<ApiResponse<Page<UserResponse>>>(`${environment.apiUrl}/auth/api/users?page=${page}&size=${size}`)
      .pipe(map(res => res.data!));
  }

  deleteUser(userId: string): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${environment.apiUrl}/auth/api/users/${userId}`)
      .pipe(map(() => void 0));
  }
}
