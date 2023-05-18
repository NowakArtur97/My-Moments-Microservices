import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from 'src/app/auth/services/user.service';

import UserAcquaintance from '../models/user-acquaintance.model';
import { FollowerService } from '../service/follower.service';
import { UserAcquaintancesComponent } from '../user-acquaintances/user-acquaintances.component';

@Component({
  selector: 'app-following',
  templateUrl: '../user-acquaintances/user-acquaintances.component.html',
  styleUrls: ['../user-acquaintances/user-acquaintances.component.css'],
})
export class FollowingComponent
  extends UserAcquaintancesComponent
  implements OnInit {
  private following: UserAcquaintance[] = [];

  constructor(
    protected followerService: FollowerService,
    protected userService: UserService,
    protected router: Router
  ) {
    super(followerService, userService, router);
    this.subject = this.followerService.myFollowing;
  }

  ngOnInit(): void {
    this.followerService.myFollowing.subscribe((following) => {
      this.following = following;
      if (this.users.length !== 0) {
        this.usersLoaded = this.following;
        return;
      }
      this.loadUsers(this.following);
    });
  }
}
