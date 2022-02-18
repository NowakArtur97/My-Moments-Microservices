import { Component } from '@angular/core';

import { AuthBaseComponent } from '../auth-base/auth-base.component';
import AuthenticationRequest from '../models/authentication-request.model';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-authentication',
  templateUrl: './authentication.component.html',
  styleUrls: [
    './authentication.component.css',
    '../auth-base/auth-base.component.css',
  ],
})
export class AuthenticationComponent extends AuthBaseComponent {
  authenticationRequest: AuthenticationRequest = {
    username: '',
    email: '',
    password: '',
  };

  constructor(protected authService: AuthService) {
    super(authService);
  }

  onSubmit(): void {
    this.authService.loginUser(this.authenticationRequest);
  }
}
