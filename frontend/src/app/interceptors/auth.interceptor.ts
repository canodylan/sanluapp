import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { throwError } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const authService = inject(AuthService);
  const isAuthCall = authService.isAuthRequest(request.url);
  const accessToken = authService.getAccessToken();
  let authRequest = request;

  if (accessToken && !isAuthCall) {
    authRequest = request.clone({
      setHeaders: {
        Authorization: `Bearer ${accessToken}`
      }
    });
  }

  let attemptedRefresh = false;

  return next(authRequest).pipe(
    catchError((error) => {
      if (error.status === 401 && !attemptedRefresh && !isAuthCall) {
        attemptedRefresh = true;
        return authService.refresh().pipe(
          switchMap((newToken) => {
            const retryRequest = request.clone({
              setHeaders: {
                Authorization: `Bearer ${newToken}`
              }
            });
            return next(retryRequest);
          }),
          catchError((refreshError) => {
            authService.clearTokens();
            return throwError(() => refreshError);
          })
        );
      }

      return throwError(() => error);
    })
  );
};
