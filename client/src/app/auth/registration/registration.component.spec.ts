import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { AppCommonModule } from 'src/app/common/common.module';

import UserRegistrationDTO from '../models/user-registration-dto.model';
import { RegistrationComponent } from './registration.component';

describe('RegistrationComponent', () => {
  let component: RegistrationComponent;
  let fixture: ComponentFixture<RegistrationComponent>;

  const defaultRegistrationData: UserRegistrationDTO = {
    username: 'Username',
    email: 'email@email.com',
    password: 'Password123!@',
    matchingPassword: 'Password123!@',
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegistrationComponent],
      imports: [FormsModule, BrowserModule, AppCommonModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RegistrationComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();

    component.ngOnInit();
    component.ngAfterViewChecked();
  });

  describe('form validation', async () => {
    beforeEach(() => {
      fixture.whenStable().then(() => {
        component.username.setValue(defaultRegistrationData.username);
        component.email.setValue(defaultRegistrationData.email);
        component.password.setValue(defaultRegistrationData.password);
        component.matchingPassword.setValue(
          defaultRegistrationData.matchingPassword
        );
      });
    });

    it('with empty username should be invalid', async () => {
      fixture.whenStable().then(() => {
        const userName = component.username;
        userName.setValue('');
        const errors = userName.errors;

        expect(errors).not.toBeNull();
        expect(errors!.required).toBeTruthy();
        expect(Object.keys({ ...errors }).length).toEqual(1);
      });
    });

    it('with too short username should be invalid', async () => {
      fixture.whenStable().then(() => {
        const userName = component.username;
        userName.setValue('123');
        const errors = userName.errors;

        expect(errors).not.toBeNull();
        expect(errors!.minlength).toBeTruthy();
        expect(Object.keys({ ...errors }).length).toEqual(1);
      });
    });

    it('with too long username should be invalid', async () => {
      fixture.whenStable().then(() => {
        const userName = component.username;
        userName.setValue('12345678901234567890123456789012345678901');
        const errors = userName.errors;

        expect(errors).not.toBeNull();
        expect(errors!.maxlength).toBeTruthy();
        expect(Object.keys({ ...errors }).length).toEqual(1);
      });
    });

    it('with empty email should be invalid', async () => {
      fixture.whenStable().then(() => {
        const email = component.email;
        email.setValue('');
        const errors = email.errors;

        expect(errors).not.toBeNull();
        expect(errors!.required).toBeTruthy();
        expect(Object.keys({ ...errors }).length).toEqual(1);
      });
    });

    it('with incorrect email should be invalid', async () => {
      fixture.whenStable().then(() => {
        const email = component.email;
        email.setValue('email.com');
        const errors = email.errors;

        expect(errors).not.toBeNull();
        expect(errors!.email).toBeTruthy();
        expect(Object.keys({ ...errors }).length).toEqual(1);
      });
    });
  });
});
