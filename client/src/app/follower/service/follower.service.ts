import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, forkJoin } from 'rxjs';
import { UserService } from 'src/app/auth/services/user.service';
import BACKEND_URLS from 'src/app/backend-urls';
import HttpService from 'src/app/common/services/http.service';
import { environment } from 'src/environments/environment.local';

import UserAcquaintance from '../models/user-acquaintance.model';
import UsersAcquaintancesResponse from '../models/users-acquaintances-response.model';
import { EXAMPLE_FOLLOWERS, EXAMPLE_FOLLOWERS_2 } from './example-followers';

@Injectable({ providedIn: 'root' })
export class FollowerService extends HttpService {
  // TODO: Delete
  // myFollowers = new BehaviorSubject<UserAcquaintance[]>(EXAMPLE_FOLLOWERS);
  // myFollowing = new BehaviorSubject<UserAcquaintance[]>(EXAMPLE_FOLLOWERS);
  myFollowers = new BehaviorSubject<UserAcquaintance[]>([]);
  myFollowing = new BehaviorSubject<UserAcquaintance[]>([]);

  constructor(
    protected httpClient: HttpClient,
    private userService: UserService
  ) {
    super(httpClient, environment.followerServiceUrl);
  }

  getAcquaintances(): void {
    const followersRequest = this.httpClient.get<UsersAcquaintancesResponse>(
      `${this.baseUrl}${BACKEND_URLS.follower.followers('username')}`
    );
    const followingRequest = this.httpClient.get<UsersAcquaintancesResponse>(
      `${this.baseUrl}${BACKEND_URLS.follower.following('username')}`
    );

    forkJoin(followersRequest, followingRequest).subscribe(
      ([{ users: followers }, { users: following }]) =>
        this.handleSuccessfulResponses(followers, following),
      (httpErrorResponse: HttpErrorResponse) => {
        this.logErrors(httpErrorResponse);
        // TODO: DELETE
        const usernames = [...EXAMPLE_FOLLOWERS, ...EXAMPLE_FOLLOWERS_2].map(
          ({ username }) => username
        );
        this.userService.getUsersPhotos(usernames);
        this.myFollowers.next(EXAMPLE_FOLLOWERS);
        this.myFollowing.next(EXAMPLE_FOLLOWERS_2);
      }
    );
  }

  private handleSuccessfulResponses(
    followers: UserAcquaintance[],
    following: UserAcquaintance[]
  ) {
    this.myFollowers.next(followers);
    this.myFollowing.next(following);
    const usernames = [...followers, ...following].map(
      ({ username }) => username
    );
    // TODO: Handle users photo
    this.userService.getUsersPhotos(usernames);
  }
}
