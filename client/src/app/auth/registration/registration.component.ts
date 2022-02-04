import { Component, OnInit, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';

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
    console.log(this.registrationForm.controls['matching_password'].errors);
  }
}
