import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, NgForm } from '@angular/forms';
import { Subscription } from 'rxjs';

import AuthenticationRequest from '../models/authentication-request.model';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-authentication',
  templateUrl: './authentication.component.html',
  styleUrls: ['./authentication.component.css'],
})
export class AuthenticationComponent implements OnInit, OnDestroy {
  @ViewChild('authenticationForm') authenticationForm!: NgForm;
  private controlsKeys = {
    username: 'username',
    email: 'email',
    password: 'password',
    matchingPassword: 'matching_password',
  };

  private registerFormSubscriptions$ = new Subscription();
  authenticationRequest: AuthenticationRequest = {
    username: '',
    email: '',
    password: '',
  };
  authErrors: string[] = [];
  authErrorsSunscription$!: Subscription;

  constructor(private athService: AuthService) {}

  ngOnInit(): void {
    this.authErrorsSunscription$ = this.athService.authError.subscribe(
      (authError) => (this.authErrors = authError?.errors || [])
    );
  }

  ngOnDestroy = (): void => this.registerFormSubscriptions$?.unsubscribe();

  onAuthenticate(): void {
    console.log(this.authenticationForm);
    this.athService.loginUser(this.authenticationRequest);
  }

  get username(): AbstractControl {
    return this.authenticationForm?.controls[this.controlsKeys.username];
  }

  get email(): AbstractControl {
    return this.authenticationForm?.controls[this.controlsKeys.email];
  }

  get password(): AbstractControl {
    return this.authenticationForm?.controls[this.controlsKeys.password];
  }
}
