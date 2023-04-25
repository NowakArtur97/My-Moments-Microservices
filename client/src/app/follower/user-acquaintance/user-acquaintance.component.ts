import { Component, Input, OnInit } from '@angular/core';

import UserAcquaintance from '../models/user-acquaintance.model';

@Component({
  selector: 'app-user-acquaintance',
  templateUrl: './user-acquaintance.component.html',
  styleUrls: ['./user-acquaintance.component.css'],
})
export class UserAcquaintanceComponent implements OnInit {
  @Input('user') user!: UserAcquaintance;

  constructor() {}

  ngOnInit(): void {}
}
