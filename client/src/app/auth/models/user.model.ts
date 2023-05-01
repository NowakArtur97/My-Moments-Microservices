interface AuthenticationResponse {
  readonly token: string;
  readonly expirationTimeInMilliseconds: number;
}

interface UserProfile {
  readonly about: string;
  readonly gender: string;
  readonly interests: string;
  readonly languages: string;
  readonly location: string;
  readonly image: string[];
}

interface Role {
  readonly name: string;
}

export default interface User {
  readonly username: string;
  readonly email: string;
  readonly authenticationResponse: AuthenticationResponse;
  readonly profile: UserProfile;
  readonly roles: Role[];
}

export { AuthenticationResponse, UserProfile, Role, User };
