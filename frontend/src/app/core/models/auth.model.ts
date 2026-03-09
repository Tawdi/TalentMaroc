// Auth Models - matching backend DTOs

// API Response wrapper
export interface ApiResponse<T> {
  status: 'SUCCESS' | 'ERROR';
  message: string;
  data: T | null;
  timestamp: number;
  path?: string;
  errors?: Record<string, string> | string[];
}

// Register Request
export interface RegisterRequest {
  email: string;
  username: string;
  password: string;
  name: string;
  roleName?: string;
}

// Login Request
export interface LoginRequest {
  email?: string;
  username?: string;
  password: string;
}

// Login Response
export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number; // in seconds
  userId: string;
  email: string;
  username: string;
  role: string;
}

// User Response (after registration)
export interface UserResponse {
  id: string;
  email: string;
  username: string;
  name: string;
  role: string;
}

// Token Response (for refresh)
export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

// Forgot Password Request
export interface ForgotPasswordRequest {
  email: string;
}

// Reset Password Request
export interface ResetPasswordRequest {
  token: string;
  newPassword: string;
}

// Verify Email Request
export interface VerifyEmailRequest {
  token: string;
}

// Refresh Token Request
export interface RefreshTokenRequest {
  refreshToken: string;
}

// User roles
export type UserRole = 'CANDIDATE' | 'COMPANY' | 'ADMIN';

// Current User (stored in state)
export interface CurrentUser {
  id: string;
  email: string;
  username: string;
  name?: string;
  role: UserRole;
  accessToken: string;
  refreshToken: string;
  expiresAt: number; // timestamp when token expires
}

