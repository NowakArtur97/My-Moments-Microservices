import { Component, Input, OnInit } from '@angular/core';

import Follower from '../models/follower.model';

@Component({
  selector: 'app-follower',
  templateUrl: './follower.component.html',
  styleUrls: ['./follower.component.css'],
})
export class FollowerComponent implements OnInit {
  @Input('follower') follower!: Follower;

  constructor() {}

  ngOnInit(): void {}
}
