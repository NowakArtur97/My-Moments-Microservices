import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { APP_ROUTES } from 'src/app/common/const.data';
import ErrorResponse from 'src/app/common/models/error-response.model';
import { environment } from 'src/environments/environment.local';

import AuthenticationRequest from '../models/authentication-request.model';
import AuthenticationResponse from '../models/authentication-response.model';
import UserRegistrationDTO from '../models/user-registration-dto.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  authError = new BehaviorSubject<ErrorResponse | null>(null);
  authenticatedUser = new BehaviorSubject<AuthenticationResponse | null>(null);

  constructor(private httpClient: HttpClient, private router: Router) {}

  registerUser(userData: UserRegistrationDTO): void {
    const multipartData = new FormData();
    multipartData.append('user', JSON.stringify(userData));
    this.httpClient
      .post<AuthenticationResponse>(
        `${environment.userServiceUrl}/registration/register`,
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
        `${environment.userServiceUrl}/authentication`,
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
    console.log(authenticationResponse);
    this.authenticatedUser.next(authenticationResponse);
    this.authError.next(null);
    this.router.navigate([`/${APP_ROUTES.posts}`]);
  }

  private handleAuthenticationErrors(
    httpErrorResponse: HttpErrorResponse
  ): void {
    console.log(httpErrorResponse);
    this.authError.next(
      this.isErrorResponse(httpErrorResponse)
        ? (httpErrorResponse.error as ErrorResponse)
        : this.getDefaultErrorResponse()
    );
  }

  private isErrorResponse(httpErrorResponse: HttpErrorResponse): boolean {
    return (httpErrorResponse.error as ErrorResponse).errors !== undefined;
  }

  private getDefaultErrorResponse(): ErrorResponse {
    return {
      status: 500,
      timestamp: new Date(),
      errors: ['Something went wrong.', 'Please try again in a moment.'],
    };
  }
}
