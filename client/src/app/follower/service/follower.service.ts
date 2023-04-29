import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, forkJoin } from 'rxjs';
import UsersPhotosResponse from 'src/app/auth/models/users-photos-response.model';
import EXAMPLE_PHOTO from 'src/app/auth/services/example-photo';
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
  myFollowers = new BehaviorSubject<UserAcquaintance[]>(EXAMPLE_FOLLOWERS);
  myFollowing = new BehaviorSubject<UserAcquaintance[]>(EXAMPLE_FOLLOWERS_2);
  // myFollowers = new BehaviorSubject<UserAcquaintance[]>([]);
  // myFollowing = new BehaviorSubject<UserAcquaintance[]>([]);

  constructor(
    protected httpClient: HttpClient,
    private userService: UserService
  ) {
    super(httpClient, environment.followerServiceUrl);
  }

  getAcquaintances(username: string = 'username'): void {
    // TODO: Get username from service
    const followersRequest = this.httpClient.get<UsersAcquaintancesResponse>(
      `${this.baseUrl}${BACKEND_URLS.follower.followers(username)}`
    );
    const followingRequest = this.httpClient.get<UsersAcquaintancesResponse>(
      `${this.baseUrl}${BACKEND_URLS.follower.following(username)}`
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

  followBack(username: string): void {
    this.httpClient
      .post<void>(
        `${this.baseUrl}${BACKEND_URLS.follower.followers(username)}`,
        {}
      )
      .subscribe(
        () => {
          const following = this.myFollowing.getValue().map((following) => {
            if (following.username === username) {
              following.isMutual = true;
            }
            return following;
          });
          this.myFollowing.next(following);
        },
        (httpErrorResponse: HttpErrorResponse) => {
          // TODO: Delete
          const following = this.myFollowing.getValue().map((following) => {
            if (following.username === username) {
              following.isMutual = true;
            }
            return following;
          });
          this.myFollowing.next(following);
          this.logErrors(httpErrorResponse);
        }
      );
  }

  unfollowUser(username: string): void {
    this.myFollowers.next(
      this.myFollowers.getValue().filter((user) => user.username !== username)
    );
    this.httpClient
      .delete<void>(
        `${this.baseUrl}${BACKEND_URLS.follower.followers(username)}`,
        {}
      )
      .subscribe(
        () => {
          this.myFollowers.next(
            this.myFollowers
              .getValue()
              .filter((user) => user.username !== username)
          );
        },
        (httpErrorResponse: HttpErrorResponse) => {
          this.logErrors(httpErrorResponse);
          // TODO: Delete;
          this.myFollowers.next(
            this.myFollowers
              .getValue()
              .filter((user) => user.username !== username)
          );
        }
      );
  }

  private handleSuccessfulResponses(
    followers: UserAcquaintance[],
    following: UserAcquaintance[]
  ) {
    const users = [...followers, ...following];
    const usernames = users.map(({ username }) => username);
    // TODO: Handle users photo
    this.userService.getUsersPhotos(usernames).subscribe(
      ({ photos }: UsersPhotosResponse) => {
        this.setUsersPhotos(followers, following, photos);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.logErrors(httpErrorResponse);
        // TODO: DELETE
        const mockPhotos: string[] = Array.from(
          Array(usernames.length)
        ).map(() => this.mapToBase64(EXAMPLE_PHOTO));
        this.setUsersPhotos(followers, following, mockPhotos);
      }
    );
  }

  private setUsersPhotos(
    followers: UserAcquaintance[],
    following: UserAcquaintance[],
    photos: string[]
  ) {
    let index = 0;
    followers.forEach((user) => {
      user.photo = photos[index];
      index++;
    });
    following.forEach((user) => {
      user.photo = photos[index];
      index++;
    });
    this.myFollowers.next(followers);
    this.myFollowing.next(following);
  }
}
