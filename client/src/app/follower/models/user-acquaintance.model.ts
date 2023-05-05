export default interface UserAcquaintance {
  readonly username: string;
  numberOfFollowing: number;
  numberOfFollowers: number;
  photo?: string;
  isMutual?: boolean;
}
