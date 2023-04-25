import { Component } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

import UserAcquaintance from '../models/user-acquaintance.model';
import { FollowerService } from '../service/follower.service';

@Component({
  template: '',
  styleUrls: ['./user-acquaintances.component.css'],
})
export abstract class UserAcquaintancesComponent {
  users: UserAcquaintance[] = [];
  subject!: BehaviorSubject<UserAcquaintance[]>;

  constructor(protected followerService: FollowerService) {}

  ngOnInit(): void {
    this.subject.subscribe((users) => (this.users = users));
  }
}
