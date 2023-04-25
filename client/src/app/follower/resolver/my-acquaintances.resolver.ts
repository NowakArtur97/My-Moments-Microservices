import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';

import Follower from '../models/follower.model';
import { FollowerService } from '../service/follower.service';

@Injectable({ providedIn: 'root' })
export default abstract class MyAcquaintancesResolver implements Resolve<any> {
  constructor(protected followerService: FollowerService) {}

  public resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Follower[] {
    const followers = this.getUsers();
    if (followers.length === 0) {
      this.fetchUsers();
      return this.getUsers();
    } else {
      return followers;
    }
  }

  abstract getUsers(): Follower[];

  abstract fetchUsers(): void;
}
