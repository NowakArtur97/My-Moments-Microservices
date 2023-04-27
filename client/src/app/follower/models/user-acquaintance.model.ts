export default interface UserAcquaintance {
  readonly username: string;
  readonly numberOfFollowing: number;
  readonly numberOfFollowers: number;
  photo?: string;
  isMutual?: boolean;
}
