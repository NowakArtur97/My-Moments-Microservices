import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { FollowersComponent } from './followers/followers.component';
import { FollowingComponent } from './following/following.component';
import { UserAcquaintanceComponent } from './user-acquaintance/user-acquaintance.component';

@NgModule({
  declarations: [
    FollowersComponent,
    FollowingComponent,
    UserAcquaintanceComponent,
  ],
  imports: [CommonModule],
})
export class FollowersModule {}
