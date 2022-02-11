import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment.local';

import AuthenticationResponse from '../models/authentication-response.model';
import UserRegistrationDTO from '../models/user-registration-dto.model';

@Injectable({ providedIn: 'root' })
export class RegistrationService {
  constructor(private httpClient: HttpClient) {}

  registerUser(userData: UserRegistrationDTO) {
    return this.httpClient
      .post<AuthenticationResponse>(
        `${environment.userServiceUrl}/registration/register`,
        userData
      )
      .subscribe((data) => {
        console.log(data);
        return data;
      });
  }
}
