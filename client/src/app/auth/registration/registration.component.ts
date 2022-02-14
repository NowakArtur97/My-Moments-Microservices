import { AfterViewChecked, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, NgForm } from '@angular/forms';
import { Subscription } from 'rxjs';

import UserRegistrationDTO from '../models/user-registration-dto.model';
import { RegistrationService } from '../services/registration.service';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css'],
})
export class RegistrationComponent
  implements OnInit, OnDestroy, AfterViewChecked {
  @ViewChild('registrationForm') registrationForm!: NgForm;

  private registerFormSubscriptions$ = new Subscription();
  userRegistrationDTO: UserRegistrationDTO = {
    username: '',
    email: '',
    password: '',
    matchingPassword: '',
  };
  authErrors: string[] = [];
  authErrorsSunscription$!: Subscription;

  constructor(private registrationService: RegistrationService) {}

  ngOnInit(): void {
    this.authErrorsSunscription$ = this.registrationService.authError.subscribe(
      (authError) => (this.authErrors = authError?.errors || [])
    );
  }

  ngOnDestroy = (): void => this.registerFormSubscriptions$?.unsubscribe();

  ngAfterViewChecked = (): void => this.refreshFormFieldsAfterChange();

  onRegister(): void {
    console.log(this.registrationForm);
    this.registrationService.registerUser(this.userRegistrationDTO);
  }

  private refreshFormFieldsAfterChange() {
    if (this.username) {
      this.registerFormSubscriptions$.add(
        this.username.valueChanges.subscribe(() => {
          this.password.updateValueAndValidity({ emitEvent: false });
          this.matchingPassword.updateValueAndValidity({ emitEvent: false });
        })
      );
      this.registerFormSubscriptions$.add(
        this.password.valueChanges.subscribe(() => {
          this.matchingPassword.updateValueAndValidity({ emitEvent: false });
        })
      );
      this.registerFormSubscriptions$.add(
        this.matchingPassword.valueChanges.subscribe(() => {
          this.password.updateValueAndValidity({ emitEvent: false });
        })
      );
    }
  }

  get username(): AbstractControl {
    return this.registrationForm?.controls['username'];
  }

  get email(): AbstractControl {
    return this.registrationForm?.controls['email'];
  }

  get password(): AbstractControl {
    return this.registrationForm?.controls['password'];
  }

  get matchingPassword(): AbstractControl {
    return this.registrationForm?.controls['matching_password'];
  }
}
