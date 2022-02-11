import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import ErrorResponse from 'src/app/common/models/error-response.model';
import { environment } from 'src/environments/environment.local';

import AuthenticationResponse from '../models/authentication-response.model';
import UserRegistrationDTO from '../models/user-registration-dto.model';

@Injectable({ providedIn: 'root' })
export class RegistrationService {
  authError = new BehaviorSubject<ErrorResponse | null>(null);
  authenticatedUser = new BehaviorSubject<AuthenticationResponse | null>(null);

  constructor(private httpClient: HttpClient) {}

  registerUser = (userData: UserRegistrationDTO) =>
    this.httpClient
      .post<AuthenticationResponse>(
        `${environment.userServiceUrl}/registration/register`,
        userData
      )
      .subscribe(
        (authenticationResponse: AuthenticationResponse) => {
          this.authenticatedUser.next(authenticationResponse);
        },
        (error: HttpErrorResponse) =>
          this.authError.next(error.error as ErrorResponse)
      );
}
