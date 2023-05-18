import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { UserService } from 'src/app/auth/services/user.service';
import { APP_ROUTES } from 'src/app/common/const.data';

import UserAcquaintance from '../models/user-acquaintance.model';
import { FollowerService } from '../service/follower.service';

@Component({
  template: '',
  styleUrls: ['./user-acquaintances.component.css'],
})
export abstract class UserAcquaintancesComponent {
  private readonly LOAD_USER_INTERVAL = 10;
  protected users: UserAcquaintance[] = [];
  usersLoaded: UserAcquaintance[] = [];
  subject!: BehaviorSubject<UserAcquaintance[]>;
  usersInterval!: number;
  isInFollowersView: boolean;

  constructor(
    protected followerService: FollowerService,
    protected userService: UserService,
    protected router: Router
  ) {
    this.isInFollowersView =
      this.router.url === `/${APP_ROUTES.follower.followers}`;
  }

  protected loadUsers(users: UserAcquaintance[]): void {
    if (users.length === 0) {
      return;
    }
    this.users = users;
    this.usersLoaded = [];
    let index = 0;
    clearInterval(this.usersInterval);
    this.usersInterval = window.setInterval(() => {
      this.usersLoaded.push(this.users[index]);
      index++;
      if (this.usersLoaded.length === this.users.length) {
        clearInterval(this.usersInterval);
      }
    }, this.LOAD_USER_INTERVAL);
  }
}
