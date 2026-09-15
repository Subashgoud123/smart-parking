import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { ApiError } from '../models';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const snack = inject(MatSnackBar);
  const token = auth.token();
  const authed = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(authed).pipe(
    catchError((err: HttpErrorResponse) => {
      const body = err.error as ApiError | undefined;
      const message = body?.message || err.statusText || 'Request failed';
      if (err.status === 401 && !req.url.includes('/api/auth/')) {
        auth.logout();
      }
      snack.open(message, 'Dismiss', { duration: 4000 });
      return throwError(() => err);
    })
  );
};
