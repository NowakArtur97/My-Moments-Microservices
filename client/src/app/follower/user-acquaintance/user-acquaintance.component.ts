import { animate, state, style, transition, trigger } from '@angular/animations';
import { Component, HostListener, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { APP_ROUTES } from 'src/app/common/const.data';

import UserAcquaintance from '../models/user-acquaintance.model';
import { FollowerService } from '../service/follower.service';

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
  private readonly HOVER_STATE = { DEFAULT: 'default', HOVER: 'hover' };

  isInFollowersView = false;
  isHovered = false;
  state = this.HOVER_STATE.DEFAULT;
  buttonState = this.HOVER_STATE.DEFAULT;

  constructor(
    private followerService: FollowerService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.isInFollowersView =
      this.router.url === `/${APP_ROUTES.follower.followers}`;
  }

  onTakeAction(): void {
    const { username } = this.user;
    if (this.isInFollowersView) {
      this.followerService.unfollowUser(username);
    } else {
      this.followerService.followBack(username);
    }
  }

  @HostListener('mouseover')
  onHover(): void {
    const areAlreadyMutual = !this.isInFollowersView && this.user.isMutual;
    if (areAlreadyMutual) {
      return;
    }
    this.setAnimationVariables(true);
  }

  @HostListener('mouseout')
  onLeave(): void {
    const areAlreadyMutual = !this.isInFollowersView && this.user.isMutual;
    if (areAlreadyMutual) {
      return;
    }
    this.setAnimationVariables(false);
  }

  private setAnimationVariables(isHovered: boolean) {
    this.isHovered = isHovered;
    this.state = isHovered ? this.HOVER_STATE.HOVER : this.HOVER_STATE.DEFAULT;
    this.buttonState = isHovered
      ? this.HOVER_STATE.HOVER
      : this.HOVER_STATE.DEFAULT;
  }
}
