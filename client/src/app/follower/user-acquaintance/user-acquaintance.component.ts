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
    trigger('userState', [
      state(
        'default',
        style({
          transform: 'scale(1)',
        })
      ),
      state(
        'delete',
        style({
          transform: 'scale(0)',
        })
      ),
      transition('default => delete', animate('0.5s')),
    ]),
    trigger('buttonState', [
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
  ],
})
export class UserAcquaintanceComponent implements OnInit {
  @Input('user') user!: UserAcquaintance;
  private readonly ANIMATION_STATE = {
    DEFAULT: 'default',
    HOVER: 'hover',
    DELETE: 'delete',
  };

  isInFollowersView = false;
  isHovered = false;
  userState = this.ANIMATION_STATE.DEFAULT;
  buttonState = this.ANIMATION_STATE.DEFAULT;
  wasUnfollowed = false;

  constructor(
    private followerService: FollowerService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.isInFollowersView =
      this.router.url === `/${APP_ROUTES.follower.followers}`;
  }

  onTakeAction(): void {
    if (this.isInFollowersView) {
      this.followerService.followBack(this.user.username);
      this.setAnimationVariables(false);
    } else {
      this.wasUnfollowed = true;
      this.userState = this.ANIMATION_STATE.DELETE;
    }
  }

  onUnfollowAnimationFinished(): void {
    if (!this.wasUnfollowed) {
      return;
    }
    this.followerService.unfollowUser(this.user.username);
  }

  @HostListener('mouseover')
  onHover(): void {
    const skipActionOnHover = this.isInFollowersView && this.user.isMutual;
    if (skipActionOnHover) {
      return;
    }
    this.setAnimationVariables(true);
  }

  @HostListener('mouseout')
  onLeave(): void {
    const skipActionOnHover = this.isInFollowersView && this.user.isMutual;
    if (skipActionOnHover) {
      return;
    }
    this.setAnimationVariables(false);
  }

  private setAnimationVariables(isHovered: boolean): void {
    this.isHovered = isHovered;
    this.buttonState = isHovered
      ? this.ANIMATION_STATE.HOVER
      : this.ANIMATION_STATE.DEFAULT;
  }
}
