import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import BACKEND_URLS from 'src/app/backend-urls';
import HttpService from 'src/app/common/services/http.service';
import { environment } from 'src/environments/environment.local';

import UsersPhotosResponse from '../models/users-photos-response.model';
import EXAMPLE_PHOTO from './example-photo';

@Injectable({
  providedIn: 'root',
})
export class UserService extends HttpService {
  usersPhotos = new BehaviorSubject<string[]>([]);

  constructor(protected httpClient: HttpClient) {
    super(httpClient, environment.userServiceUrl);
  }

  getUsersPhotos(usernames: string[]) {
    let params = new HttpParams().set('usernames', usernames.join(','));

    this.httpClient
      .get<UsersPhotosResponse>(`${this.baseUrl}${BACKEND_URLS.user.users}`, {
        params,
      })
      .subscribe(
        ({ photos }: UsersPhotosResponse) =>
          this.usersPhotos.next(photos.map((photo) => this.mapToBase64(photo))),
        (httpErrorResponse: HttpErrorResponse) => {
          this.logErrors(httpErrorResponse);
          // TODO: DELETE
          const mockPhotos: string[] = Array.from(
            Array(usernames.length)
          ).map(() => this.mapToBase64(EXAMPLE_PHOTO));
          this.usersPhotos.next(mockPhotos);
        }
      );
  }
}
