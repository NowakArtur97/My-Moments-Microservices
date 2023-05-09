import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import BACKEND_URLS from 'src/app/backend-urls';
import HttpService from 'src/app/common/services/http.service';
import { environment } from 'src/environments/environment.local';

import UsersPhotosResponse from '../models/users-photos-response.model';

@Injectable({
  providedIn: 'root',
})
export class UserService extends HttpService {
  constructor(protected httpClient: HttpClient) {
    super(httpClient, environment.userServiceUrl);
  }

  getUsersPhotos(usernames: string[]): Observable<UsersPhotosResponse> {
    const params = new HttpParams().set('usernames', usernames.join(','));
    return this.httpClient.get<UsersPhotosResponse>(
      `${this.baseUrl}${BACKEND_URLS.user.photos}`,
      {
        params,
      }
    );
  }
}
