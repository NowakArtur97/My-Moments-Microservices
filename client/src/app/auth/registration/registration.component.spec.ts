import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { AppCommonModule } from 'src/app/common/common.module';

import { PasswordRulesDirective } from '../directives/password-rules.directive';
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
      declarations: [RegistrationComponent, PasswordRulesDirective],
      imports: [
        FormsModule,
        BrowserModule,
        AppCommonModule,
        HttpClientTestingModule,
      ],
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

    it('with username with white spaces should be invalid', async () => {
      fixture.whenStable().then(() => {
        const userName = component.username;
        userName.setValue('user1 23');
        const errors = userName.errors;

        expect(errors).not.toBeNull();
        expect(errors!.hasWhiteSpaces).toBeTruthy();
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

    it('with email with white spaces should be invalid', async () => {
      fixture.whenStable().then(() => {
        const email = component.email;
        email.setValue('email 123@email.com');
        const errors = email.errors;

        expect(errors).not.toBeNull();
        expect(errors!.email).toBeTruthy();
        expect(Object.keys({ ...errors }).length).toEqual(1);
      });
    });

    it('with empty password should be invalid', async () => {
      fixture.whenStable().then(() => {
        const password = component.password;
        password.setValue('');
        const errors = password.errors;

        expect(errors).not.toBeNull();
        expect(errors!.required).toBeTruthy();
        expect(errors!.mustMatch).toBeTruthy();
        expect(errors!.withoutLowerCase).toBeTruthy();
        expect(errors!.withoutUpperCase).toBeTruthy();
        expect(errors!.withoutSpecialCharacter).toBeTruthy();
        expect(Object.keys({ ...errors }).length).toEqual(5);
      });
    });

    it('with white spaces password should be invalid', async () => {
      fixture.whenStable().then(() => {
        const password = component.password;
        password.setValue('Pass 123@');
        const errors = password.errors;

        expect(errors).not.toBeNull();
        expect(errors!.hasWhiteSpaces).toBeTruthy();
        expect(errors!.mustMatch).toBeTruthy();
        expect(Object.keys({ ...errors }).length).toEqual(2);
      });
    });

    it('without small letter password should be invalid', async () => {
      fixture.whenStable().then(() => {
        const password = component.password;
        password.setValue('PASS123@');
        const errors = password.errors;

        expect(errors).not.toBeNull();
        expect(errors!.withoutLowerCase).toBeTruthy();
        expect(errors!.mustMatch).toBeTruthy();
        expect(Object.keys({ ...errors }).length).toEqual(2);
      });
    });

    it('without upper letter password should be invalid', async () => {
      fixture.whenStable().then(() => {
        const password = component.password;
        password.setValue('pass123@');
        const errors = password.errors;

        expect(errors).not.toBeNull();
        expect(errors!.withoutUpperCase).toBeTruthy();
        expect(errors!.mustMatch).toBeTruthy();
        expect(Object.keys({ ...errors }).length).toEqual(2);
      });
    });

    it('without special character password should be invalid', async () => {
      fixture.whenStable().then(() => {
        const password = component.password;
        password.setValue('pass123PASS');
        const errors = password.errors;

        expect(errors).not.toBeNull();
        expect(errors!.withoutSpecialCharacter).toBeTruthy();
        expect(errors!.mustMatch).toBeTruthy();
        expect(Object.keys({ ...errors }).length).toEqual(2);
      });
    });

    it('with three repetead characters password should be invalid', async () => {
      fixture.whenStable().then(() => {
        const password = component.password;
        password.setValue('aaaASD@');
        const errors = password.errors;

        expect(errors).not.toBeNull();
        expect(errors!.with3RepeatedCharacters).toBeTruthy();
        expect(errors!.mustMatch).toBeTruthy();
        expect(Object.keys({ ...errors }).length).toEqual(2);
      });
    });

    it('with common password should be invalid', async () => {
      fixture.whenStable().then(() => {
        const password = component.password;
        password.setValue('password');
        const errors = password.errors;

        expect(errors).not.toBeNull();
        expect(errors!.commonPassword).toBeTruthy();
        expect(errors!.withoutUpperCase).toBeTruthy();
        expect(errors!.withoutSpecialCharacter).toBeTruthy();
        expect(errors!.mustMatch).toBeTruthy();
        expect(Object.keys({ ...errors }).length).toEqual(4);
      });
    });

    it('with empty matching password should be invalid', async () => {
      fixture.whenStable().then(() => {
        const matchingPassword = component.matchingPassword;
        matchingPassword.setValue('');
        const errors = matchingPassword.errors;

        expect(errors).not.toBeNull();
        expect(errors!.required).toBeTruthy();
        expect(errors!.mustMatch).toBeTruthy();
        expect(errors!.withoutLowerCase).toBeTruthy();
        expect(errors!.withoutUpperCase).toBeTruthy();
        expect(errors!.withoutSpecialCharacter).toBeTruthy();
        expect(Object.keys({ ...errors }).length).toEqual(5);
      });
    });

    it('with white spaces matching password should be invalid', async () => {
      fixture.whenStable().then(() => {
        const matchingPassword = component.matchingPassword;
        matchingPassword.setValue('Pass 123@');
        const errors = matchingPassword.errors;

        expect(errors).not.toBeNull();
        expect(errors!.hasWhiteSpaces).toBeTruthy();
        expect(errors!.mustMatch).toBeTruthy();
        expect(Object.keys({ ...errors }).length).toEqual(2);
      });
    });

    it('without small letter matching password should be invalid', async () => {
      fixture.whenStable().then(() => {
        const matchingPassword = component.matchingPassword;
        matchingPassword.setValue('PASS123@');
        const errors = matchingPassword.errors;

        expect(errors).not.toBeNull();
        expect(errors!.withoutLowerCase).toBeTruthy();
        expect(errors!.mustMatch).toBeTruthy();
        expect(Object.keys({ ...errors }).length).toEqual(2);
      });
    });

    it('without upper letter matching password should be invalid', async () => {
      fixture.whenStable().then(() => {
        const matchingPassword = component.matchingPassword;
        matchingPassword.setValue('pass123@');
        const errors = matchingPassword.errors;

        expect(errors).not.toBeNull();
        expect(errors!.withoutUpperCase).toBeTruthy();
        expect(errors!.mustMatch).toBeTruthy();
        expect(Object.keys({ ...errors }).length).toEqual(2);
      });
    });

    it('without special character matching password should be invalid', async () => {
      fixture.whenStable().then(() => {
        const matchingPassword = component.matchingPassword;
        matchingPassword.setValue('pass123PASS');
        const errors = matchingPassword.errors;

        expect(errors).not.toBeNull();
        expect(errors!.withoutSpecialCharacter).toBeTruthy();
        expect(errors!.mustMatch).toBeTruthy();
        expect(Object.keys({ ...errors }).length).toEqual(2);
      });
    });

    it('with three repetead characters matching password should be invalid', async () => {
      fixture.whenStable().then(() => {
        const matchingPassword = component.matchingPassword;
        matchingPassword.setValue('aaaASD@');
        const errors = matchingPassword.errors;

        expect(errors).not.toBeNull();
        expect(errors!.with3RepeatedCharacters).toBeTruthy();
        expect(errors!.mustMatch).toBeTruthy();
        expect(Object.keys({ ...errors }).length).toEqual(2);
      });
    });

    it('with common matching matching password should be invalid', async () => {
      fixture.whenStable().then(() => {
        const matchingPassword = component.matchingPassword;
        matchingPassword.setValue('password');
        const errors = matchingPassword.errors;

        expect(errors).not.toBeNull();
        expect(errors!.commonPassword).toBeTruthy();
        expect(errors!.withoutUpperCase).toBeTruthy();
        expect(errors!.withoutSpecialCharacter).toBeTruthy();
        expect(errors!.mustMatch).toBeTruthy();
        expect(Object.keys({ ...errors }).length).toEqual(4);
      });
    });

    it('with not matched passwords should be invalid', async () => {
      fixture.whenStable().then(() => {
        const password = component.password;
        const matchingPassword = component.matchingPassword;
        password.setValue('validPass123@');
        matchingPassword.setValue('123validPass@');
        const errors = password.errors;

        expect(errors).not.toBeNull();
        expect(errors!.mustMatch).toBeTruthy();
        expect(Object.keys({ ...errors }).length).toEqual(1);
      });
    });

    it('with password containing username should be invalid', async () => {
      fixture.whenStable().then(() => {
        const password = component.password;
        component.username.setValue('User');
        password.setValue('userX123@');
        const errors = password.errors;

        expect(errors).not.toBeNull();
        expect(errors!.shouldNotInclude).toBeTruthy();
        expect(errors!.mustMatch).toBeTruthy();
        expect(Object.keys({ ...errors }).length).toEqual(2);
      });
    });
  });
});
