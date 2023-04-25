import { Injectable } from '@angular/core';

import UserAcquaintance from '../models/user-acquaintance.model';
import { FollowerService } from '../service/follower.service';
import MyAcquaintancesResolver from './my-acquaintances.resolver';

@Injectable({ providedIn: 'root' })
export default class MyFollowersResolver extends MyAcquaintancesResolver {
  constructor(protected followerService: FollowerService) {
    super(followerService);
  }

  getUsers = (): UserAcquaintance[] =>
    this.followerService.myFollowers.getValue();

  fetchUsers = (): void => this.followerService.getMyFollowers();
}
