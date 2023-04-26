import { Component } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { UserService } from 'src/app/auth/services/user.service';

import UserAcquaintance from '../models/user-acquaintance.model';
import { FollowerService } from '../service/follower.service';

@Component({
  template: '',
  styleUrls: ['./user-acquaintances.component.css'],
})
export abstract class UserAcquaintancesComponent {
  private readonly LOAD_USER_INTERVAL = 10;
  private users: UserAcquaintance[] = [];
  usersLoaded: UserAcquaintance[] = [];
  subject!: BehaviorSubject<UserAcquaintance[]>;
  usersInterval!: NodeJS.Timeout;
  arePhotosLoaded = false;

  constructor(
    protected followerService: FollowerService,
    protected userService: UserService
  ) {}

  ngOnInit(): void {
    this.subject.subscribe((users) => (this.users = users));
    this.userService.usersPhotos.subscribe((usersPhotos) => {
      this.users = this.users.map((user, index) => {
        return { ...user, photo: usersPhotos[index] };
      });
      this.arePhotosLoaded = usersPhotos.length > 0;
      this.usersLoaded = [];
      this.loadUsers();
    });
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
