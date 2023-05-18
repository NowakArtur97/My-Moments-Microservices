import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, forkJoin } from 'rxjs';
import { UserPhotoModel, UsersPhotosResponse } from 'src/app/auth/models/users-photos-response.model';
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

    forkJoin([followersRequest, followingRequest]).subscribe(
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

  private updateUsers(username: string): void {
    const following = this.myFollowing.getValue().map((user) =>
      user.username === username
        ? {
            ...user,
            isMutual: true,
            numberOfFollowing: user.numberOfFollowing + 1,
          }
        : user
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
  ): void {
    const usernames = [...followers, ...following].map(
      ({ username }) => username
    );
    this.userService.getUsersPhotos(usernames).subscribe(
      ({ usersPhotos }: UsersPhotosResponse) => {
        this.setUsersProperties(followers, following, usersPhotos);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.logErrors(httpErrorResponse);
        // TODO: DELETE
        const mockPhotos: UserPhotoModel[] = Array.from(
          Array(usernames.length)
        ).map((username) => {
          return { username, image: EXAMPLE_PHOTO };
        });
        this.setUsersProperties(followers, following, mockPhotos);
      }
    );
  }

  private setUsersProperties(
    followers: UserAcquaintance[],
    following: UserAcquaintance[],
    usersPhotos: UserPhotoModel[]
  ): void {
    const followersWithProperties = this.setProperties(
      followers,
      following,
      usersPhotos
    );
    const followingWithProperties = this.setProperties(
      following,
      followers,
      usersPhotos
    );
    this.myFollowers.next(followersWithProperties);
    this.myFollowing.next(followingWithProperties);
  }

  private setProperties = (
    users: UserAcquaintance[],
    usersToCheckAgainst: UserAcquaintance[],
    usersPhotos: UserPhotoModel[]
  ) =>
    users.map((user) => {
      const isMutual = usersToCheckAgainst.some(
        (userToCheckAgainst) => userToCheckAgainst.username === user.username
      );
      const photo = usersPhotos.find(
        (userPhoto) => userPhoto.username === user.username
      )!!.image;
      return {
        ...user,
        photo: this.mapToBase64(photo === '' ? EXAMPLE_PHOTO : photo),
        isMutual,
      };
    });
}
