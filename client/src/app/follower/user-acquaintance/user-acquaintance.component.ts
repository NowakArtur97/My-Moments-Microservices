import { animate, state, style, transition, trigger } from '@angular/animations';
import { Component, HostListener, Input, OnInit } from '@angular/core';

import UserAcquaintance from '../models/user-acquaintance.model';

@Component({
  selector: 'app-user-acquaintance',
  templateUrl: './user-acquaintance.component.html',
  styleUrls: ['./user-acquaintance.component.css'],
  animations: [
    trigger('state', [
      state(
        'default',
        style({
          transform: 'translateY(0%)',
        })
      ),
      state(
        'hover',
        style({
          transform: 'translateY(-100%)',
        })
      ),
      transition('default <=> hover', animate('0.5s')),
    ]),
    trigger('buttonState', [
      state(
        'default',
        style({
          transform: 'translateY(-100%)',
        })
      ),
      state(
        'hover',
        style({
          transform: 'translateY(0%)',
        })
      ),
      transition('default <=> hover', animate('0.5s')),
    ]),
  ],
})
export class UserAcquaintanceComponent implements OnInit {
  @Input('user') user!: UserAcquaintance;

  isHovered = false;
  state = 'default';
  buttonState = 'default';

  constructor() {}

  ngOnInit(): void {}

  @HostListener('mouseover')
  onHover(): void {
    this.isHovered = true;
    this.state = 'hover';
    this.buttonState = 'hover';
  }

  @HostListener('mouseout')
  onLeave(): void {
    this.isHovered = false;
    this.state = 'default';
    this.buttonState = 'default';
  }
}
