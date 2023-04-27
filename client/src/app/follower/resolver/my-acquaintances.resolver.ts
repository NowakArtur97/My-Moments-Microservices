import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';

import UserAcquaintance from '../models/user-acquaintance.model';
import { FollowerService } from '../service/follower.service';

// TODO: Delete child classes?
@Injectable({ providedIn: 'root' })
export default abstract class MyAcquaintancesResolver implements Resolve<any> {
  constructor(protected followerService: FollowerService) {}

  public resolve(): UserAcquaintance[] {
    const users = this.getUsers();
    if (users.length === 0) {
      this.followerService.getAcquaintances();
      return this.getUsers();
    } else {
      return users;
    }
  }

  abstract getUsers(): UserAcquaintance[];
}
