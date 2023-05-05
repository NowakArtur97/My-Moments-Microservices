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
  // myFollowers = new BehaviorSubject<UserAcquaintance[]>(EXAMPLE_FOLLOWERS);
  // myFollowing = new BehaviorSubject<UserAcquaintance[]>(EXAMPLE_FOLLOWERS_2);
  myFollowers = new BehaviorSubject<UserAcquaintance[]>([]);
  myFollowing = new BehaviorSubject<UserAcquaintance[]>([]);

  constructor(
    protected httpClient: HttpClient,
    private userService: UserService
  ) {
    super(httpClient, environment.followerServiceUrl);
  }

  getAcquaintances(username: string): void {
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
        this.handleSuccessfulResponses(EXAMPLE_FOLLOWERS, EXAMPLE_FOLLOWERS_2);
      }
    );
  }

  followBack(username: string): void {
    this.httpClient
      .post<void>(
        `${this.baseUrl}${BACKEND_URLS.follower.following(username)}`,
        {}
      )
      .subscribe(
        () => this.updateUsers(username),
        (httpErrorResponse: HttpErrorResponse) => {
          // TODO: Delete
          this.updateUsers(username);
          this.logErrors(httpErrorResponse);
        }
      );
  }

  private updateUsers(username: string) {
    const following = this.myFollowing.getValue().map((following) =>
      following.username === username
        ? {
            ...following,
            isMutual: true,
            numberOfFollowing: following.numberOfFollowing + 1,
          }
        : following
    );
    const followers = this.myFollowers.getValue().map((follower) =>
      follower.username === username
        ? {
            ...follower,
            isMutual: true,
            numberOfFollowers: follower.numberOfFollowers + 1,
          }
        : follower
    );
    this.myFollowing.next(following);
    this.myFollowers.next(followers);
  }

  unfollowUser(username: string): void {
    this.myFollowers.next(
      this.myFollowers.getValue().filter((user) => user.username !== username)
    );
    this.httpClient
      .delete<void>(
        `${this.baseUrl}${BACKEND_URLS.follower.following(username)}`,
        {}
      )
      .subscribe(
        () => {
          this.myFollowing.next(
            this.myFollowing
              .getValue()
              .filter((user) => user.username !== username)
          );
        },
        (httpErrorResponse: HttpErrorResponse) => {
          this.logErrors(httpErrorResponse);
          // TODO: Delete;
          this.myFollowing.next(
            this.myFollowing
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
    const usernames = [...followers, ...following].map(
      ({ username }) => username
    );
    this.userService.getUsersPhotos(usernames).subscribe(
      ({ photos }: UsersPhotosResponse) => {
        this.setUsersProperties(followers, following, photos);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.logErrors(httpErrorResponse);
        // TODO: DELETE
        const mockPhotos: string[] = Array.from(
          Array(usernames.length)
        ).map(() => this.mapToBase64(EXAMPLE_PHOTO));
        this.setUsersProperties(followers, following, mockPhotos);
      }
    );
  }

  private setUsersProperties(
    followers: UserAcquaintance[],
    following: UserAcquaintance[],
    photos: string[]
  ) {
    let index = 0;
    const followersWithProperties = followers.map((user) => {
      const isMutual = following.some(
        (userToCheckAgainst) => userToCheckAgainst.username === user.username
      );
      return {
        ...user,
        photo: photos[index++],
        isMutual,
      };
    });
    const followingWithProperties = following.map((user) => {
      const isMutual = followers.some(
        (userToCheckAgainst) => userToCheckAgainst.username === user.username
      );
      return {
        ...user,
        photo: photos[index++],
        isMutual,
      };
    });
    this.myFollowers.next(followersWithProperties);
    this.myFollowing.next(followingWithProperties);
  }
}
