import AuthenticationRequest from './authentication-request.model';

interface UserRegistrationDTO extends AuthenticationRequest {
  matchingPassword: string;
  profile: UserProfileDTO;
}

interface UserProfileDTO {
  about: string;
  gender: string;
  interests: string;
  languages: string;
  location: string;
}

export { UserRegistrationDTO };
