import { Component, OnInit } from '@angular/core';

import UserRegistrationDTO from '../models/user-registration-dto.model';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css'],
})
export class RegistrationComponent implements OnInit {
  userRegistrationDTO: UserRegistrationDTO = {
    username: '',
    email: '',
    password: '',
    matchingPassword: '',
  };

  constructor() {}

  ngOnInit(): void {}

  onRegister(): void {
    console.log(this.userRegistrationDTO);
  }
}
