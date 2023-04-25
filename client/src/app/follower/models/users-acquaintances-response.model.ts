import Follower from './follower.model';

export default interface UsersAcquaintancesResponse {
  readonly followers: Follower[];
}
