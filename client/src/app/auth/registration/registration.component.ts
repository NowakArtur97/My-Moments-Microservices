import { animate, state, style, transition, trigger } from '@angular/animations';
import { AfterViewChecked, Component, HostListener } from '@angular/core';
import { AbstractControl } from '@angular/forms';
import { Subscription } from 'rxjs';
import { AUTH_FORMS_WIDTH_TO_TWO_COLUMNS_CHANGE } from 'src/app/common/const.data';

import { AuthBaseComponent } from '../auth-base/auth-base.component';
import { UserRegistrationDTO } from '../models/user-registration.dto';
import { AuthService } from '../services/auth.service';
import EXAMPLE_PHOTO from '../services/example-photo';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['../auth-base/auth-base.component.css'],
  animations: [
    trigger('step', [
      state(
        'first',
        style({
          top: '0',
        })
      ),
      state(
        'second',
        style({
          top: '-100%',
        })
      ),
      state(
        'third',
        style({
          top: '-200%',
        })
      ),
      transition('first <=> second', [animate('500ms ease-in-out')]),
      transition('second <=> third', [animate('500ms ease-in-out')]),
    ]),
  ],
})
export class RegistrationComponent
  extends AuthBaseComponent
  implements AfterViewChecked {
  private registerFormSubscriptions$ = new Subscription();
  private readonly DEFAULT_GENDER = 'UNSPECIFIED';
  userRegistrationDTO: UserRegistrationDTO = {
    username: '',
    email: '',
    password: '',
    matchingPassword: '',
    profile: {
      about: '',
      gender: this.DEFAULT_GENDER,
      interests: '',
      languages: '',
      location: '',
    },
  };
  step = 'third';
  image: File | null = null;
  imagePicture: string = '';

  constructor(protected authService: AuthService) {
    super(authService);
  }

  ngOnInit(): void {
    this.imagePicture = this.authService.mapToBase64(EXAMPLE_PHOTO);
  }

  ngAfterViewChecked = (): void => this.refreshFormFieldsAfterChange();

  onContinueRegistration(step: string): void {
    this.step = step;
  }

  onSubmit = (): void =>
    this.authService.registerUser(this.userRegistrationDTO, this.image);

  onSetProfileImage(image: FileList | null): void {
    if (image) {
      this.loadFileToImage(image);
    }
  }

  onChoseGender(event: Event): void {
    const radioButton = <HTMLInputElement>event.target;
    if (radioButton.checked) {
      const { gender } = this.userRegistrationDTO.profile;
      const isSameValue = gender === radioButton.value;
      if (isSameValue) {
        this.userRegistrationDTO.profile.gender = this.DEFAULT_GENDER;
        radioButton.checked = false;
      } else {
        this.userRegistrationDTO.profile.gender = radioButton.value;
      }
    }
  }

  private loadFileToImage(image: FileList) {
    this.image = image[0];
    const fileReader = new FileReader();
    fileReader.onloadend = (event: any) =>
      (this.imagePicture = event.target.result);
    fileReader.readAsDataURL(this.image);
  }

  setupAnimationValues(): void {
    this.setupLeftOffset();
    this.hiddenLeftValueInPercentage = 100;
  }

  @HostListener('window:resize', ['$event'])
  onResize = (): void => this.setupLeftOffset();

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

  private setupLeftOffset(): void {
    this.presentLeftValueInPercentage =
      window.innerWidth > AUTH_FORMS_WIDTH_TO_TWO_COLUMNS_CHANGE ? 50 : 0;
  }
}
