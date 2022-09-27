import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import HttpService from 'src/app/common/services/http.service';
import { environment } from 'src/environments/environment.local';

import ImageSnippet from '../models/image-snippet.model';
import Post from '../models/post.model';
import examplePosts from './example-posts';

@Injectable({ providedIn: 'root' })
export class PostService extends HttpService {
  // myPosts = new BehaviorSubject<Post[]>([]);
  // TODO: DELETE
  myPosts = new BehaviorSubject<Post[]>(examplePosts);

  constructor(protected httpClient: HttpClient, private router: Router) {
    super(httpClient);
  }

  createPost(files: ImageSnippet[]): void {
    const multipartData = this.createFormdata([
      { key: 'photos', value: files },
      // TODO: PostService: add caption
      //  { key: 'post', value: { caption: 'aaaaa' } },
    ]);
    this.httpClient
      .post<Post>(`${environment.postServiceUrl}`, multipartData)
      .subscribe(
        (newPost: Post) => this.handleSuccessfullPostCreation(newPost),
        (httpErrorResponse: HttpErrorResponse) =>
          this.handleErrors(httpErrorResponse)
      );
  }

  private handleSuccessfullPostCreation(newPost: Post): void {
    this.myPosts.next([...this.myPosts.getValue(), newPost]);
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
