import { AfterViewChecked, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, NgForm } from '@angular/forms';
import { Subscription } from 'rxjs';

import UserRegistrationDTO from '../models/user-registration-dto.model';

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

  constructor() {}

  ngOnInit(): void {}

  ngOnDestroy = (): void => this.registerFormSubscriptions$?.unsubscribe();

  ngAfterViewChecked = (): void => this.refreshFormFieldsAfterChange();

  onRegister(): void {
    if (this.registrationForm.controls['matching_password'].errors) {
      console.log(this.password.errors);
    }
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
