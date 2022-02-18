import AuthenticationRequest from './authentication-request.model';

export default interface UserRegistrationDTO extends AuthenticationRequest {
  matchingPassword: string;
}
