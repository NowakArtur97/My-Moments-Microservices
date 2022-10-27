import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { exhaustMap, map, take } from 'rxjs/operators';

import BACKEND_URLS from '../backend-urls';
import { AuthService } from './services/auth.service';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return this.authService.authenticatedUser.pipe(
      take(1),
      map((authenticatedUser) => authenticatedUser?.token),
      exhaustMap((token) => {
        const { url } = request;
        const isNotSecured =
          url.includes(`${BACKEND_URLS.user.authentication}`) ||
          url.includes(`${BACKEND_URLS.user.registration}`);
        if (isNotSecured) {
          return next.handle(request);
        } else {
          const clonedRequest = request.clone({
            headers: request.headers.append('Authorization', `Bearer ${token}`),
          });
          return next.handle(clonedRequest);
        }
      })
    );
  }
}
