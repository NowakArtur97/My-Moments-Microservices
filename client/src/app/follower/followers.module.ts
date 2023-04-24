import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { FollowerComponent } from './follower/follower.component';
import { FollowersComponent } from './followers/followers.component';

@NgModule({
  declarations: [FollowersComponent, FollowerComponent],
  imports: [CommonModule],
})
export class FollowersModule {}
