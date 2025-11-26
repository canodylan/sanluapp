import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { finalize, map, shareReplay, tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';

export interface LoginPayload {
  username: string;
  password: string;
}

export interface RegisterPayload {
  username: string;
  password: string;
  email: string;
  nickname?: string;
  firstName?: string;
  lastName?: string;
  phoneNumber?: string;
}

export interface AuthenticatedUser {
  id?: number;
  username: string;
  email: string;
  nickname?: string | null;
  name?: string | null;
  firstName?: string | null;
  lastName?: string | null;
  phoneNumber?: string | null;
  birthday?: string | null;
  roles?: string[];
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  user: AuthenticatedUser;
}

export interface RefreshResponse {
  accessToken: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private readonly accessKey = 'sanluapp_access_token';
  private readonly refreshKey = 'sanluapp_refresh_token';
  private readonly userKey = 'sanluapp_user';
  private readonly userSubject = new BehaviorSubject<AuthenticatedUser | null>(this.readStoredUser());
  readonly user$ = this.userSubject.asObservable();
  private refreshInFlight$: Observable<string> | null = null;

  login(username: string, password: string): Observable<LoginResponse> {
    const body: LoginPayload = { username, password };
    return this.http.post<LoginResponse>(`${environment.authUrl}/login`, body).pipe(
      tap((res) => this.handleAuthSuccess(res))
    );
  }

  register(payload: RegisterPayload): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${environment.authUrl}/register`, payload).pipe(
      tap((res) => this.handleAuthSuccess(res))
    );
  }

  refresh(): Observable<string> {
    const refreshToken = this.getRefreshToken();
    if (!refreshToken) {
      return throwError(() => new Error('Missing refresh token'));
    }

    if (!this.refreshInFlight$) {
      this.refreshInFlight$ = this.http
        .post<RefreshResponse>(`${environment.authUrl}/refresh`, { refreshToken })
        .pipe(
          map((res) => res.accessToken),
          tap((accessToken) => this.saveAccessToken(accessToken)),
          shareReplay({ bufferSize: 1, refCount: false }),
          finalize(() => {
            this.refreshInFlight$ = null;
          })
        );
    }

    return this.refreshInFlight$;
  }

  logout(): Observable<void> {
    const refreshToken = this.getRefreshToken();
    if (!refreshToken) {
      this.clearTokens();
      return of(void 0);
    }

    return this.http
      .post<void>(`${environment.authUrl}/logout`, { refreshToken })
      .pipe(finalize(() => this.clearTokens()));
  }

  saveTokens(accessToken: string, refreshToken: string): void {
    this.saveAccessToken(accessToken);
    localStorage.setItem(this.refreshKey, refreshToken);
  }

  saveUser(user: AuthenticatedUser): void {
    localStorage.setItem(this.userKey, JSON.stringify(user));
    this.userSubject.next(user);
  }

  getCurrentUser(): AuthenticatedUser | null {
    return this.userSubject.value;
  }

  getAccessToken(): string | null {
    return localStorage.getItem(this.accessKey);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(this.refreshKey);
  }

  clearTokens(): void {
    localStorage.removeItem(this.accessKey);
    localStorage.removeItem(this.refreshKey);
    localStorage.removeItem(this.userKey);
    this.userSubject.next(null);
  }

  isAuthRequest(url: string): boolean {
    return url.startsWith(environment.authUrl) || url.includes('/auth/');
  }

  isLoggedIn(): boolean {
    return !!this.getAccessToken();
  }

  hasRole(role: string): boolean {
    if (!role) return false;
    const user = this.getCurrentUser();
    if (!user || !user.roles?.length) return false;
    const target = this.normalizeRole(role);
    if (!target) return false;
    return user.roles.some((assigned) => this.normalizeRole(assigned) === target);
  }

  hasAnyRole(...roles: string[]): boolean {
    if (!roles?.length) return false;
    const user = this.getCurrentUser();
    if (!user || !user.roles?.length) return false;

    const normalizedTargets = roles
      .map((role) => this.normalizeRole(role))
      .filter((val): val is string => !!val);

    if (!normalizedTargets.length) return false;

    return user.roles.some((assigned) => {
      const normalizedAssigned = this.normalizeRole(assigned);
      if (!normalizedAssigned) return false;
      return normalizedTargets.includes(normalizedAssigned);
    });
  }

  private normalizeRole(role?: string): string | null {
    if (!role) return null;
    let normalized = role.trim().toUpperCase();
    if (!normalized) return null;
    if (normalized.startsWith('ROLE_')) {
      normalized = normalized.substring(5);
    }
    return normalized;
  }

  private saveAccessToken(accessToken: string): void {
    localStorage.setItem(this.accessKey, accessToken);
  }

  private handleAuthSuccess(response: LoginResponse): void {
    this.saveTokens(response.accessToken, response.refreshToken);
    this.saveUser(response.user);
  }

  private readStoredUser(): AuthenticatedUser | null {
    const stored = localStorage.getItem(this.userKey);
    if (!stored) return null;
    try {
      return JSON.parse(stored) as AuthenticatedUser;
    } catch {
      return null;
    }
  }
}
