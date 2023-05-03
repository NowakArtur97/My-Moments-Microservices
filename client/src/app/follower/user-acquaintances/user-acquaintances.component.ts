import { Component, OnInit } from '@angular/core';
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
export abstract class UserAcquaintancesComponent implements OnInit {
  private readonly LOAD_USER_INTERVAL = 10;
  private following: UserAcquaintance[] = [];
  private followers: UserAcquaintance[] = [];
  private users: UserAcquaintance[] = [];
  usersLoaded: UserAcquaintance[] = [];
  subject!: BehaviorSubject<UserAcquaintance[]>;
  usersInterval!: NodeJS.Timeout;
  isInFollowersView: boolean;

  constructor(
    protected followerService: FollowerService,
    protected userService: UserService,
    protected router: Router
  ) {
    this.isInFollowersView =
      this.router.url === `/${APP_ROUTES.follower.followers}`;
  }

  ngOnInit(): void {
    this.followerService.myFollowers.subscribe((followers) => {
      this.followers = followers;
      const wasAlreadyLoaded =
        this.users.length !== 0 && this.isInFollowersView;
      if (wasAlreadyLoaded || this.followers.length === 0) {
        this.usersLoaded = this.followers;
        return;
      }
      this.setUsersBasedOnView();
      this.usersLoaded = [];
      this.loadUsers();
    });
    this.followerService.myFollowing.subscribe((following) => {
      this.following = following;
      const wasAlreadyLoaded =
        this.users.length !== 0 && !this.isInFollowersView;
      if (wasAlreadyLoaded || this.following.length === 0) {
        this.usersLoaded = this.following;
        return;
      }
      this.setUsersBasedOnView();
      this.usersLoaded = [];
      this.loadUsers();
    });
  }

  private setUsersBasedOnView(): void {
    if (this.isInFollowersView) {
      this.users = this.followers;
    } else {
      this.users = this.following;
    }
  }

  private loadUsers(): void {
    let index = 0;
    clearInterval(this.usersInterval);
    this.usersInterval = setInterval(() => {
      this.usersLoaded.push(this.users[index]);
      index++;
      if (this.usersLoaded.length === this.users.length) {
        clearInterval(this.usersInterval);
      }
    }, this.LOAD_USER_INTERVAL);
  }
}
