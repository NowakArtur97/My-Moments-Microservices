import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import ErrorResponse from 'src/app/common/models/error-response.model';
import { environment } from 'src/environments/environment.local';

import ImageSnippet from '../models/image-snippet.model';
import PostResponse from '../models/post-response-model';

@Injectable({ providedIn: 'root' })
export class PostService {
  constructor(private httpClient: HttpClient) {}

  createPost(files: ImageSnippet[]) {
    const multipartData = new FormData();
    multipartData.append('photos', JSON.stringify(files));
    // TODO: PostService: add caption
    // multipartData.append('post', '{"caption": "aaaaa"}');
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

  private isErrorResponse = (httpErrorResponse: HttpErrorResponse): boolean =>
    (httpErrorResponse.error as ErrorResponse)?.errors !== undefined;
}
