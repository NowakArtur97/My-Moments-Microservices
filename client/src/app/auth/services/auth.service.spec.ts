import { HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { getTestBed, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { skip } from 'rxjs/operators';
import { APP_ROUTES } from 'src/app/common/const.data';
import ErrorResponse from 'src/app/common/models/error-response.model';
import { environment } from 'src/environments/environment.local';

import AuthenticationRequest from '../models/authentication-request.model';
import { UserRegistrationDTO } from '../models/user-registration.dto';
import User from '../models/user.model';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let injector: TestBed;
  let authService: AuthService;
  let httpMock: HttpTestingController;
  let router: Router;
  let navigateSpy: jasmine.Spy;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [AuthService],
    });
  });

  beforeEach(() => {
    injector = getTestBed();
    authService = injector.inject(AuthService);
    httpMock = injector.inject(HttpTestingController);
    router = injector.inject(Router);

    navigateSpy = spyOn(router, 'navigate');
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('when register user', () => {
    it('with correct data should register user, emit authenticated user data, clear auth errors and redirect', () => {
      const registrationData: UserRegistrationDTO = {
        username: 'username',
        email: 'email@email.com',
        password: 'Password123!',
        matchingPassword: 'Password123!',
        profile: {
          about: '',
          gender: '',
          interests: '',
          languages: '',
          location: '',
        },
      };
      const authResponse: User = {
        username: 'username',
        email: 'username@email.com',
        profile: {
          about: 'about',
          gender: 'UNSPECIFIED',
          interests: 'interests',
          languages: 'languages',
          location: 'location',
          image: ['image'],
        },
        authenticationResponse: {
          token: 'token',
          expirationTimeInMilliseconds: 72000000,
        },
        roles: [{ name: 'USER_ROLE' }],
      };

      authService.authenticatedUser.pipe(skip(1)).subscribe((res) => {
        expect(res).toEqual(authResponse);
      });
      authService.authError.pipe(skip(1)).subscribe((res) => {
        expect(res).toEqual(null);
      });
      authService.registerUser(registrationData);

      const req = httpMock.expectOne(
        `${environment.userServiceUrl}/registration/register`
      );
      expect(req.request.method).toBe('POST');
      req.flush(authResponse);

      expect(navigateSpy).toHaveBeenCalledWith([`/${APP_ROUTES.post.posts}`]);
    });

    it('with incorrect data should emit auth errors', () => {
      const registrationData: UserRegistrationDTO = {
        username: 'username',
        email: 'incorrectEmail.com',
        password: 'incorrectData',
        matchingPassword: 'incorrectData',
        profile: {
          about: '',
          gender: '',
          interests: '',
          languages: '',
          location: '',
        },
      };
      const errorResponse: ErrorResponse = {
        errors: ['Invalid data'],
        status: 400,
        timestamp: new Date(),
      };
      const httpErrorResponse: HttpErrorResponse = new HttpErrorResponse({
        error: errorResponse,
        status: 400,
        headers: new HttpHeaders(),
        statusText: 'OK',
        url: '',
      });

      authService.authError.pipe(skip(2)).subscribe((res) => {
        expect(res).toEqual(errorResponse);
      });
      authService.registerUser(registrationData);

      const req = httpMock.expectOne(
        `${environment.userServiceUrl}/registration/register`
      );
      expect(req.request.method).toBe('POST');
      req.flush(httpErrorResponse);
    });
  });

  describe('when authenticate user', () => {
    it('with correct data should authenticate user, emit authenticated user data, clear auth errors and redirect', () => {
      const authenticationRequest: AuthenticationRequest = {
        username: 'username',
        email: 'email@email.com',
        password: 'Password123!',
      };
      const authResponse: User = {
        username: 'username',
        email: 'username@email.com',
        profile: {
          about: 'about',
          gender: 'UNSPECIFIED',
          interests: 'interests',
          languages: 'languages',
          location: 'location',
          image: ['image'],
        },
        authenticationResponse: {
          token: 'token',
          expirationTimeInMilliseconds: 72000000,
        },
        roles: [{ name: 'USER_ROLE' }],
      };

      authService.authenticatedUser.pipe(skip(1)).subscribe((res) => {
        expect(res).toEqual(authResponse);
      });
      authService.authError.pipe(skip(1)).subscribe((res) => {
        expect(res).toEqual(null);
      });
      authService.loginUser(authenticationRequest);

      const req = httpMock.expectOne(
        `${environment.userServiceUrl}/authentication`
      );
      expect(req.request.method).toBe('POST');
      req.flush(authResponse);

      expect(navigateSpy).toHaveBeenCalledWith([`/${APP_ROUTES.post.posts}`]);
    });

    it('with incorrect data should emit auth errors', () => {
      const authenticationRequest: AuthenticationRequest = {
        username: 'username',
        email: 'incorrectEmail.com',
        password: 'incorrectData',
      };
      const errorResponse: ErrorResponse = {
        errors: ['Invalid data'],
        status: 400,
        timestamp: new Date(),
      };
      const httpErrorResponse: HttpErrorResponse = new HttpErrorResponse({
        error: errorResponse,
        status: 400,
        headers: new HttpHeaders(),
        statusText: 'OK',
        url: '',
      });

      authService.authError.pipe(skip(2)).subscribe((res) => {
        expect(res).toEqual(errorResponse);
      });
      authService.loginUser(authenticationRequest);

      const req = httpMock.expectOne(
        `${environment.userServiceUrl}/authentication`
      );
      expect(req.request.method).toBe('POST');
      req.flush(httpErrorResponse);
    });
  });
});
