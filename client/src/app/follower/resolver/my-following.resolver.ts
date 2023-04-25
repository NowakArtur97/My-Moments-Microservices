import { Injectable } from '@angular/core';

import Follower from '../models/follower.model';
import { FollowerService } from '../service/follower.service';
import MyAcquaintancesResolver from './my-acquaintances.resolver';

@Injectable({ providedIn: 'root' })
export default class MyFollowersResolver extends MyAcquaintancesResolver {
  constructor(protected followerService: FollowerService) {
    super(followerService);
  }

  getUsers = (): Follower[] => this.followerService.myFollowing.getValue();

  fetchUsers = (): void => this.followerService.getMyFollowing();
}
