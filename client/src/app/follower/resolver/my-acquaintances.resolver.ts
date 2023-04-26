import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';

import UserAcquaintance from '../models/user-acquaintance.model';
import { FollowerService } from '../service/follower.service';

@Injectable({ providedIn: 'root' })
export default abstract class MyAcquaintancesResolver implements Resolve<any> {
  constructor(protected followerService: FollowerService) {}

  public resolve(): UserAcquaintance[] {
    const followers = this.getUsers();
    if (followers.length === 0) {
      this.fetchUsers();
      return this.getUsers();
    } else {
      return followers;
    }
  }

  abstract getUsers(): UserAcquaintance[];

  abstract fetchUsers(): void;
}
