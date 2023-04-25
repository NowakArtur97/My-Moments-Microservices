import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';

import Follower from '../models/follower.model';
import { FollowerService } from '../service/follower.service';

@Injectable({ providedIn: 'root' })
export default class MyFollowersResolver implements Resolve<any> {
  constructor(private followerService: FollowerService) {}

  public resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Follower[] {
    const followers = this.followerService.myFollowers.getValue();
    if (followers.length === 0) {
      this.followerService.getMyFollowers();
      return this.followerService.myFollowers.getValue();
    } else {
      return followers;
    }
  }
}
