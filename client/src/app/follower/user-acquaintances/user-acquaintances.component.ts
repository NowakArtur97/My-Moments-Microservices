import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { UserService } from 'src/app/auth/services/user.service';
import { APP_ROUTES } from 'src/app/common/const.data';

import UserAcquaintance from '../models/user-acquaintance.model';
import { EXAMPLE_FOLLOWERS, EXAMPLE_FOLLOWERS_2 } from '../service/example-followers';
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
    // TODO: Delete
    this.following = EXAMPLE_FOLLOWERS;
    this.followers = EXAMPLE_FOLLOWERS_2;
    this.users =
      this.router.url === APP_ROUTES.follower.followers
        ? this.followers
        : this.following;
    this.usersToCheckAgainst =
      this.router.url === APP_ROUTES.follower.followers
        ? this.following
        : this.followers;
    this.users = this.users.map((user) => {
      return {
        ...user,
        isMutual: this.usersToCheckAgainst.some(
          (userToCheckAgainst) => userToCheckAgainst.username === user.username
        ),
      };
    });
    console.log(this.users);
    this.usersLoaded = [];
    this.arePhotosLoaded = true;
    this.loadUsers();

    // this.subject.subscribe((users) => (this.users = users));
    // this.userService.usersPhotos.subscribe((usersPhotos) => {
    //   this.users = this.users.map((user, index) => {
    //     return { ...user, photo: usersPhotos[index] };
    //   });
    //   this.arePhotosLoaded = usersPhotos.length > 0;
    //   this.usersLoaded = [];
    //   this.loadUsers();
    // });
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
