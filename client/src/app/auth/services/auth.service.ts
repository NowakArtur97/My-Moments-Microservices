import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import ErrorResponse from 'src/app/common/models/error-response.model';
import { environment } from 'src/environments/environment.local';

import AuthenticationResponse from '../models/authentication-response.model';
import UserRegistrationDTO from '../models/user-registration-dto.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  authError = new BehaviorSubject<ErrorResponse | null>(null);
  authenticatedUser = new BehaviorSubject<AuthenticationResponse | null>(null);

  constructor(private httpClient: HttpClient) {}

  registerUser(userData: UserRegistrationDTO): void {
    const multipartData = new FormData();
    multipartData.append('user', JSON.stringify(userData));
    this.httpClient
      .post<AuthenticationResponse>(
        `${environment.userServiceUrl}/registration/register`,
        multipartData
      )
      .subscribe(
        (authenticationResponse: AuthenticationResponse) => {
          this.authenticatedUser.next(authenticationResponse);
          this.authError.next(null);
        },
        (error: HttpErrorResponse) => {
          console.log(error);
          this.authError.next(error.error as ErrorResponse);
        }
      );
  }
}
