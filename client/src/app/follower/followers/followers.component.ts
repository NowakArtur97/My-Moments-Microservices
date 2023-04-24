import { Component, OnInit } from '@angular/core';

import Follower from '../models/follower.model';
import EXAMPLE_FOLLOWERS from '../service/example-followers';

@Component({
  selector: 'app-followers',
  templateUrl: './followers.component.html',
  styleUrls: ['./followers.component.css'],
})
export class FollowersComponent implements OnInit {
  followers: Follower[] = [];

  constructor() {}

  ngOnInit(): void {
    this.followers = EXAMPLE_FOLLOWERS;
  }
}
