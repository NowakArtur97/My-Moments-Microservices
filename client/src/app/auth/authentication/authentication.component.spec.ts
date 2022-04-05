import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';
import { AppCommonModule } from 'src/app/common/common.module';

import AuthenticationRequest from '../models/authentication-request.model';
import { AuthenticationComponent } from './authentication.component';

describe('AuthenticationComponent', () => {
  let component: AuthenticationComponent;
  let fixture: ComponentFixture<AuthenticationComponent>;

  const defaultAuthenticationRequest: AuthenticationRequest = {
    username: 'Username',
    email: 'email@email.com',
    password: 'Password123!@',
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AuthenticationComponent],
      imports: [
        FormsModule,
        BrowserModule,
        AppCommonModule,
        HttpClientTestingModule,
        RouterTestingModule,
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AuthenticationComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();

    component.ngOnInit();
  });

  beforeEach(() => {
    fixture.whenRenderingDone().then(() => {
      component.username.setValue(defaultAuthenticationRequest.username);
      component.email.setValue(defaultAuthenticationRequest.email);
      component.password.setValue(defaultAuthenticationRequest.password);
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

  it('with empty password should be invalid', async () => {
    fixture.whenStable().then(() => {
      const password = component.password;
      password.setValue('');
      const errors = password.errors;

      expect(errors).not.toBeNull();
      expect(errors!.required).toBeTruthy();
      expect(Object.keys({ ...errors }).length).toEqual(1);
    });
  });
});
