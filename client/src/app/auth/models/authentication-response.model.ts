export default interface AuthenticationResponse {
  readonly token: string;
  readonly expirationTimeInMilliseconds: number;
}
