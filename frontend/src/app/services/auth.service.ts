import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { finalize, map, shareReplay, tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';

export interface LoginPayload {
  username: string;
  password: string;
}

export interface AuthenticatedUser {
  id?: number;
  username: string;
  email: string;
  nickname?: string;
  name?: string;
  firstName?: string;
  lastName?: string;
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
  private refreshInFlight$: Observable<string> | null = null;

  login(username: string, password: string): Observable<LoginResponse> {
    const body: LoginPayload = { username, password };
    return this.http.post<LoginResponse>(`${environment.authUrl}/login`, body).pipe(
      tap((res) => this.saveTokens(res.accessToken, res.refreshToken))
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

  getAccessToken(): string | null {
    return localStorage.getItem(this.accessKey);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(this.refreshKey);
  }

  clearTokens(): void {
    localStorage.removeItem(this.accessKey);
    localStorage.removeItem(this.refreshKey);
  }

  isAuthRequest(url: string): boolean {
    return url.startsWith(environment.authUrl) || url.includes('/auth/');
  }

  isLoggedIn(): boolean {
    return !!this.getAccessToken();
  }

  private saveAccessToken(accessToken: string): void {
    localStorage.setItem(this.accessKey, accessToken);
  }
}
