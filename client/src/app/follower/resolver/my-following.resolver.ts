import { Injectable } from '@angular/core';

import UserAcquaintance from '../models/user-acquaintance.model';
import { FollowerService } from '../service/follower.service';
import MyAcquaintancesResolver from './my-acquaintances.resolver';

@Injectable({ providedIn: 'root' })
export default class MyFollowingResolver extends MyAcquaintancesResolver {
  constructor(protected followerService: FollowerService) {
    super(followerService);
  }

  getUsers = (): UserAcquaintance[] =>
    this.followerService.myFollowing.getValue();
}
