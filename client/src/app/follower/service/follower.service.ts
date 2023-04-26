import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { UserService } from 'src/app/auth/services/user.service';
import BACKEND_URLS from 'src/app/backend-urls';
import HttpService from 'src/app/common/services/http.service';
import { environment } from 'src/environments/environment.local';

import UserAcquaintance from '../models/user-acquaintance.model';
import UsersAcquaintancesResponse from '../models/users-acquaintances-response.model';
import EXAMPLE_FOLLOWERS from './example-followers';

@Injectable({ providedIn: 'root' })
export class FollowerService extends HttpService {
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

  getMyFollowers = (): void =>
    this.getUsers(
      `${BACKEND_URLS.follower.followers('username')}`, // TODO: Get username
      this.myFollowers
    );

  getMyFollowing = (): void =>
    this.getUsers(
      `${BACKEND_URLS.follower.following('username')}`, // TODO: Get username
      this.myFollowing
    );

  private getUsers(
    url: String,
    subject: BehaviorSubject<UserAcquaintance[]>
  ): void {
    this.httpClient
      .get<UsersAcquaintancesResponse>(`${this.baseUrl}${url}`)
      .subscribe(
        ({ users }: UsersAcquaintancesResponse) => {
          subject.next(users);
          const usernames = users.map(({ username }) => username);
          this.userService.getUsersPhotos(usernames);
        },
        (httpErrorResponse: HttpErrorResponse) => {
          this.logErrors(httpErrorResponse);
          // TODO: DELETE
          const usernames = EXAMPLE_FOLLOWERS.map(({ username }) => username);
          this.userService.getUsersPhotos(usernames);
          subject.next(EXAMPLE_FOLLOWERS);
        }
      );
  }
}
