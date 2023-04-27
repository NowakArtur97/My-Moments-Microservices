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
  private following: UserAcquaintance[] = [];
  private followers: UserAcquaintance[] = [];
  private users: UserAcquaintance[] = [];
  private usersToCheckAgainst: UserAcquaintance[] = [];
  usersLoaded: UserAcquaintance[] = [];
  subject!: BehaviorSubject<UserAcquaintance[]>;
  usersInterval!: NodeJS.Timeout;
  arePhotosLoaded = false;

  constructor(
    protected followerService: FollowerService,
    protected userService: UserService,
    protected router: Router
  ) {}

  ngOnInit(): void {
    this.followerService.myFollowers.subscribe(
      (followers) => (this.followers = followers)
    );
    this.followerService.myFollowing.subscribe(
      (following) => (this.following = following)
    );
    this.userService.usersPhotos.subscribe((usersPhotos) => {
      this.setUsersBasedOnView();
      this.users = this.users.map((user, index) =>
        this.mapToUserWithPhoto(user, usersPhotos, index)
      );
      this.arePhotosLoaded = usersPhotos.length > 0; // TODO: Delete?
      this.usersLoaded = [];
      this.loadUsers();
    });
  }

  private setUsersBasedOnView() {
    const isInFollowersView = this.router.url === APP_ROUTES.follower.followers;
    if (isInFollowersView) {
      this.users = this.followers;
      this.usersToCheckAgainst = this.following;
    } else {
      this.users = this.following;
      this.usersToCheckAgainst = this.followers;
    }
  }

  private mapToUserWithPhoto(
    user: UserAcquaintance,
    usersPhotos: string[],
    index: number
  ): UserAcquaintance {
    const isMutual = this.usersToCheckAgainst.some(
      (userToCheckAgainst) => userToCheckAgainst.username === user.username
    );
    return {
      ...user,
      photo: usersPhotos[index],
      isMutual,
    };
  }

  private loadUsers(): void {
    let index = 0;
    if (!this.arePhotosLoaded) {
      return;
    }
    this.usersInterval = setInterval(() => {
      this.usersLoaded.push(this.users[index]);
      index++;
      if (this.usersLoaded.length === this.users.length) {
        clearInterval(this.usersInterval);
      }
    }, this.LOAD_USER_INTERVAL);
  }
}
