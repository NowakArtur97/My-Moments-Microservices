import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import BACKEND_URLS from 'src/app/backend-urls';
import HttpService from 'src/app/common/services/http.service';
import { environment } from 'src/environments/environment.local';

import Follower from '../models/follower.model';
import UsersAcquaintancesResponse from '../models/users-acquaintances-response.model';
import EXAMPLE_FOLLOWERS from './example-followers';

@Injectable({ providedIn: 'root' })
export class FollowerService extends HttpService {
  myFollowers = new BehaviorSubject<Follower[]>(EXAMPLE_FOLLOWERS);

  constructor(protected httpClient: HttpClient) {
    super(httpClient);
  }

  getMyFollowers(): void {
    this.httpClient
      .get<UsersAcquaintancesResponse>(
        `${environment.followerServiceUrl}${BACKEND_URLS.follower.followers(
          'username'
        )}` // TODO: Get username
      )
      .subscribe(
        ({ followers }: UsersAcquaintancesResponse) =>
          this.myFollowers.next(followers),
        (httpErrorResponse: HttpErrorResponse) => {
          this.logErrors(httpErrorResponse);
          // TODO: DELETE
          this.myFollowers.next(EXAMPLE_FOLLOWERS);
        }
      );
  }
}
