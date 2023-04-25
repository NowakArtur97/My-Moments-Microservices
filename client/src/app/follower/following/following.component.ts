import { Component, OnInit } from '@angular/core';

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
  constructor(protected followerService: FollowerService) {
    super(followerService);
    this.subject = this.followerService.myFollowing;
  }
}
