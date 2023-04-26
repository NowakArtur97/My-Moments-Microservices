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
  users: UserAcquaintance[] = [];
  subject!: BehaviorSubject<UserAcquaintance[]>;

  constructor(
    protected followerService: FollowerService,
    protected userService: UserService
  ) {}

  ngOnInit(): void {
    this.subject.subscribe((users) => (this.users = users));
    this.userService.usersPhotos.subscribe(
      (usersPhotos) =>
        (this.users = this.users.map((user, index) => {
          return { ...user, photo: usersPhotos[index] };
        }))
    );
  }
}
