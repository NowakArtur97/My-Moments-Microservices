import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import HttpService from 'src/app/common/services/http.service';
import { environment } from 'src/environments/environment.local';

import ImageSnippet from '../models/image-snippet.model';
import PostResponse from '../models/post-response.model';

@Injectable({ providedIn: 'root' })
export class PostService extends HttpService {
  constructor(protected httpClient: HttpClient) {
    super(httpClient);
  }

  createPost(files: ImageSnippet[]): void {
    const multipartData = this.createFormdata([
      { key: 'photos', value: files },
      // TODO: PostService: add caption
      //  { key: 'post', value: { caption: 'aaaaa' } },
    ]);
    this.httpClient
      .post<PostResponse>(`${environment.postServiceUrl}`, multipartData)
      .subscribe(
        (res: PostResponse) => {
          console.log(res);
          return null;
        },
        (httpErrorResponse: HttpErrorResponse) =>
          this.handleErrors(httpErrorResponse)
      );
  }

  private handleErrors(httpErrorResponse: HttpErrorResponse): void {
    if (this.isErrorResponse(httpErrorResponse)) {
      console.log(true);
      console.log(httpErrorResponse);
    } else {
      console.log(httpErrorResponse);
    }
  }
}
