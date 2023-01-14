import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import BACKEND_URLS from 'src/app/backend-urls';
import { APP_ROUTES } from 'src/app/common/const.data';
import ErrorResponse from 'src/app/common/models/error-response.model';
import HttpService from 'src/app/common/services/http.service';
import { environment } from 'src/environments/environment.local';

import AuthenticationRequest from '../models/authentication-request.model';
import AuthenticationResponse from '../models/authentication-response.model';
import UserRegistrationDTO from '../models/user-registration.dto';

@Injectable({ providedIn: 'root' })
export class AuthService extends HttpService {
  authError = new BehaviorSubject<ErrorResponse | null>(null);
  // TODO: DELETE
  authenticatedUser = new BehaviorSubject<AuthenticationResponse | null>({
    token:
      'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJuZXdVc2VyIiwiZXhwIjoxNjczNzUzMDU2LCJpYXQiOjE2NzM2ODEwNTZ9.jz-fYBAlVyL7gKDGDOs8aYYnjFioEWbAPuSPWtg_E2Q',
    expirationTimeInMilliseconds: 72000000,
  });
  // authenticatedUser = new BehaviorSubject<AuthenticationResponse | null>(null);

  constructor(protected httpClient: HttpClient, private router: Router) {
    super(httpClient);
  }

  registerUser(userData: UserRegistrationDTO): void {
    const multipartData = this.createFormData([
      { key: 'user', value: userData },
    ]);
    this.httpClient
      .post<AuthenticationResponse>(
        `${environment.userServiceUrl}${BACKEND_URLS.user.registration}`,
        multipartData
      )
      .subscribe(
        (authenticationResponse: AuthenticationResponse) =>
          this.handleSuccessfullAuthentication(authenticationResponse),
        (httpErrorResponse: HttpErrorResponse) =>
          this.handleAuthenticationErrors(httpErrorResponse)
      );
  }

  loginUser(authenticationRequest: AuthenticationRequest): void {
    this.httpClient
      .post<AuthenticationResponse>(
        `${environment.userServiceUrl}${BACKEND_URLS.user.authentication}`,
        authenticationRequest
      )
      .subscribe(
        (authenticationResponse: AuthenticationResponse) =>
          this.handleSuccessfullAuthentication(authenticationResponse),
        (httpErrorResponse: HttpErrorResponse) =>
          this.handleAuthenticationErrors(httpErrorResponse)
      );
  }

  private handleSuccessfullAuthentication(
    authenticationResponse: AuthenticationResponse
  ): void {
    this.authenticatedUser.next(authenticationResponse);
    this.authError.next(null);
    this.router.navigate([`/${APP_ROUTES.post.posts}`]);
  }

  private handleAuthenticationErrors(
    httpErrorResponse: HttpErrorResponse
  ): void {
    this.authError.next(
      this.isErrorResponse(httpErrorResponse)
        ? (httpErrorResponse.error as ErrorResponse)
        : this.defaultErrorResponse
    );
  }
}
