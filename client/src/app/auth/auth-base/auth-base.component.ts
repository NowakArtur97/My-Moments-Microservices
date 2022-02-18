import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, NgForm } from '@angular/forms';
import { Subscription } from 'rxjs';

import { AuthService } from '../services/auth.service';

@Component({
  template: '',
  styleUrls: ['./auth-base.component.css'],
})
export abstract class AuthBaseComponent implements OnInit, OnDestroy {
  @ViewChild('authForm') authForm!: NgForm;

  authErrors: string[] = [];
  private authErrorsSunscription$!: Subscription;

  constructor(protected authService: AuthService) {}

  ngOnInit(): void {
    this.authErrorsSunscription$ = this.authService.authError.subscribe(
      (authError) => (this.authErrors = authError?.errors || [])
    );
  }

  ngOnDestroy = (): void => this.authErrorsSunscription$.unsubscribe();

  abstract onSubmit(): void;

  get username(): AbstractControl {
    const controlName = 'username';
    return this.authForm?.controls[controlName];
  }

  get email(): AbstractControl {
    const controlName = 'email';
    return this.authForm?.controls[controlName];
  }

  get password(): AbstractControl {
    const controlName = 'password';
    return this.authForm?.controls[controlName];
  }
}
