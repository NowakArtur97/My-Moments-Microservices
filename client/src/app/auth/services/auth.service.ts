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
import { UserRegistrationDTO } from '../models/user-registration.dto';
import User from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class AuthService extends HttpService {
  authError = new BehaviorSubject<ErrorResponse | null>(null);
  // TODO: DELETE
  authenticatedUser = new BehaviorSubject<User | null>({
    username: 'newUser',
    email: 'username@email.com',
    profile: {
      about: 'about',
      gender: 'UNSPECIFIED',
      interests: 'interests',
      languages: 'languages',
      location: 'location',
      image: ['image'],
    },
    authenticationResponse: {
      token:
        'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJuZXdVc2VyIiwiZXhwIjoxNjgzNzgwNTU4LCJpYXQiOjE2ODM3MDg1NTh9.sI4rrDwlswiRfV2PQ7OapFuvlpEZZEYlquUn82TxDHg',
      expirationTimeInMilliseconds: 72000000,
    },
    roles: [{ name: 'USER_ROLE' }],
  });
  // authenticatedUser = new BehaviorSubject<User | null>(null);

  constructor(protected httpClient: HttpClient, private router: Router) {
    super(httpClient, environment.userServiceUrl);
  }

  registerUser(userData: UserRegistrationDTO): void {
    const multipartData = this.createFormData([
      { key: 'user', value: userData },
    ]);
    this.httpClient
      .post<User>(
        `${this.baseUrl}${BACKEND_URLS.user.registration}`,
        multipartData
      )
      .subscribe(
        (authenticationResponse: User) =>
          this.handleSuccessfullAuthentication(authenticationResponse),
        (httpErrorResponse: HttpErrorResponse) =>
          this.handleAuthenticationErrors(httpErrorResponse)
      );
  }

  loginUser(authenticationRequest: AuthenticationRequest): void {
    this.httpClient
      .post<User>(
        `${this.baseUrl}${BACKEND_URLS.user.authentication}`,
        authenticationRequest
      )
      .subscribe(
        (authenticationResponse: User) =>
          this.handleSuccessfullAuthentication(authenticationResponse),
        (httpErrorResponse: HttpErrorResponse) =>
          this.handleAuthenticationErrors(httpErrorResponse)
      );
  }

  private handleSuccessfullAuthentication(authenticationResponse: User): void {
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
