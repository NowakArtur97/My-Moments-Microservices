import { Component, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, NgForm } from '@angular/forms';

import UserRegistrationDTO from '../models/user-registration-dto.model';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css'],
})
export class RegistrationComponent implements OnInit {
  @ViewChild('registrationForm', { static: true }) registrationForm!: NgForm;

  userRegistrationDTO: UserRegistrationDTO = {
    username: '',
    email: '',
    password: '',
    matchingPassword: '',
  };

  constructor() {}

  ngOnInit(): void {}

  onRegister(): void {
    if (this.registrationForm.controls['matching_password'].errors) {
      console.log(this.password.errors);
    }
  }

  get username(): AbstractControl {
    return this.registrationForm.controls['username'];
  }

  get email(): AbstractControl {
    return this.registrationForm.controls['email'];
  }

  get password(): AbstractControl {
    return this.registrationForm.controls['password'];
  }

  get matchingPassword(): AbstractControl {
    return this.registrationForm.controls['matchingPassword'];
  }
}
