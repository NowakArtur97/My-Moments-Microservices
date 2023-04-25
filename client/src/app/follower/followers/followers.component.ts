import { Component, OnInit } from '@angular/core';

import Follower from '../models/follower.model';
import { FollowerService } from '../service/follower.service';

@Component({
  selector: 'app-followers',
  templateUrl: './followers.component.html',
  styleUrls: ['./followers.component.css'],
})
export class FollowersComponent implements OnInit {
  followers: Follower[] = [];

  constructor(private followerService: FollowerService) {}

  ngOnInit(): void {
    this.followerService.myFollowers.subscribe(
      (followers) => (this.followers = followers)
    );
  }
}
