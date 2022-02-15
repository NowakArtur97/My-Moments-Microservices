import { HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { getTestBed, TestBed } from '@angular/core/testing';
import { skip } from 'rxjs/operators';
import ErrorResponse from 'src/app/common/models/error-response.model';
import { environment } from 'src/environments/environment.local';

import AuthenticationResponse from '../models/authentication-response.model';
import UserRegistrationDTO from '../models/user-registration-dto.model';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let injector: TestBed;
  let authService: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService],
    });
  });

  beforeEach(() => {
    injector = getTestBed();
    authService = injector.inject(AuthService);
    httpMock = injector.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('when register user', () => {
    it('with correct data should register user and emit authenticated user data and clear auth errors', () => {
      const registrationData: UserRegistrationDTO = {
        username: 'username',
        email: 'email@email.com',
        password: 'Password123!',
        matchingPassword: 'Password123!',
      };
      const authResponse: AuthenticationResponse = {
        token: 'token',
        expirationTimeInMilliseconds: 3600000,
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
    });

    it('with incorrect data should register user and emit auth errors', () => {
      const registrationData: UserRegistrationDTO = {
        username: 'username',
        email: 'email@email.com',
        password: 'Password123!',
        matchingPassword: 'Password123!',
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
});
