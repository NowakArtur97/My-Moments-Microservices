import { HttpEvent, HttpHandler, HttpHeaders, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { exhaustMap, map, take } from 'rxjs/operators';

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
          request.url.includes('authentication') ||
          request.url.includes('registration');
        return isNotSecured || !!token
          ? next.handle(request)
          : next.handle(
              request.clone({
                headers: new HttpHeaders().set(
                  'Authorization',
                  `Bearer ${token}`
                ),
              })
            );
      })
    );
  }
}
