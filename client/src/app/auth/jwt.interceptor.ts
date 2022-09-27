import { HttpEvent, HttpHandler, HttpHeaders, HttpInterceptor, HttpRequest } from '@angular/common/http';
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
        const isNotSecured =
          request.url.includes(`${BACKEND_URLS.user.authentication}`) ||
          request.url.includes(`${BACKEND_URLS.user.registration}`);
        return isNotSecured
          ? next.handle(request)
          : next.handle(
              request.clone({
                headers: new HttpHeaders({
                  ...request.headers,
                  Authorization: `Bearer ${token}`,
                }),
              })
            );
      })
    );
  }
}
