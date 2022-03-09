import { AfterViewChecked, Component, HostListener } from '@angular/core';
import { AbstractControl } from '@angular/forms';
import { Subscription } from 'rxjs';

import { AuthBaseComponent } from '../auth-base/auth-base.component';
import UserRegistrationDTO from '../models/user-registration-dto.model';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['../auth-base/auth-base.component.css'],
})
export class RegistrationComponent
  extends AuthBaseComponent
  implements AfterViewChecked {
  private registerFormSubscriptions$ = new Subscription();
  userRegistrationDTO: UserRegistrationDTO = {
    username: '',
    email: '',
    password: '',
    matchingPassword: '',
  };

  constructor(protected authService: AuthService) {
    super(authService);
  }

  ngAfterViewChecked = (): void => this.refreshFormFieldsAfterChange();

  onSubmit(): void {
    this.authService.registerUser(this.userRegistrationDTO);
  }

  setupAnimationValues(): void {
    this.presentLeftValue = window.innerWidth > 890 ? 50 : 0;
    this.hiddenLeftValue = 100;
  }

  @HostListener('window:resize', ['$event'])
  onResize(): void {
    this.presentLeftValue = window.innerWidth > 890 ? 50 : 0;
  }

  private refreshFormFieldsAfterChange(): void {
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

  get matchingPassword(): AbstractControl {
    const controlName = 'matching_password';
    return this.authForm?.controls[controlName];
  }
}
