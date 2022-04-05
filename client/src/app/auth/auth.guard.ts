import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { map, take } from 'rxjs/operators';

import { ROUTES } from '../common/const.data';
import { AuthService } from './services/auth.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private router: Router, private autService: AuthService) {}

  canActivate(): Observable<true | UrlTree> {
    return this.autService.authenticatedUser.pipe(
      take(1),
      map((authenticatedUser) => {
        if (authenticatedUser) {
          return true;
        } else {
          return this.router.createUrlTree([`/${ROUTES.auth}`]);
        }
      })
    );
  }
}
