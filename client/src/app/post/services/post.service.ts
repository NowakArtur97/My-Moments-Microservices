import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import BACKEND_URLS from 'src/app/backend-urls';
import { APP_ROUTES } from 'src/app/common/const.data';
import HttpService from 'src/app/common/services/http.service';
import { environment } from 'src/environments/environment.local';

import ImageSnippet from '../models/image-snippet.model';
import PostElement from '../models/post-element.model';
import PostState from '../models/post-state.enum';
import Post from '../models/post.model';
import PostsResponse from '../models/posts-response.model';
import EXAMPLE_POSTS from './example-posts';

@Injectable({ providedIn: 'root' })
export class PostService extends HttpService {
  // myPosts = new BehaviorSubject<Post[]>([]);
  // TODO: DELETE
  myPosts = new BehaviorSubject<Post[]>(EXAMPLE_POSTS);

  constructor(protected httpClient: HttpClient, private router: Router) {
    super(httpClient);
  }

  createPost(imageSnippets: ImageSnippet[]): void {
    const files = imageSnippets.map((file) => file.file);
    const multipartData = this.createFormDataFromFiles([
      { key: 'photos', files },
      // TODO: add caption
      //  { key: 'post', value: { caption: 'aaaaa' } },
    ]);
    this.httpClient
      .post<Post>(`${environment.postServiceUrl}`, multipartData)
      .subscribe(
        (newPost: Post) => this.handleSuccessfullPostsResponse([newPost]),
        (httpErrorResponse: HttpErrorResponse) =>
          this.handleErrors(httpErrorResponse)
      );
  }

  getMyPosts(): void {
    this.httpClient
      .get<PostsResponse>(
        `${environment.postServiceUrl}${BACKEND_URLS.common.myResource}`
      )
      .subscribe(
        (postsResponse: PostsResponse) =>
          this.handleSuccessfullPostsResponse(postsResponse.posts),
        (httpErrorResponse: HttpErrorResponse) =>
          this.handleErrors(httpErrorResponse)
      );
  }

  mapPostsToElements = (posts: Post[]): PostElement[] =>
    posts.map((post) => ({
      ...post,
      currentPhotoIndex: 0,
      state: PostState.INACTIVE,
      stoppedBeingActive: false,
      isCurrentlyLastElement: false,
    }));

  private handleSuccessfullPostsResponse(newPosts: Post[]): void {
    const mappedBinaryToJpgsPosts: Post[] = this.mapBinaryToJpgs(newPosts).map(
      (post) => {
        return { ...post, currentPhotoIndex: 0 };
      }
    );
    this.myPosts.next([...this.myPosts.getValue(), ...mappedBinaryToJpgsPosts]);
    this.router.navigate([`/${APP_ROUTES.post.posts}`]);
  }

  // TODO: make private
  mapBinaryToJpgs = (posts: Post[]): Post[] =>
    posts.map((post: Post) => {
      return {
        ...post,
        photos: post.photos.map((photo) => `data:image/jpg;base64,${photo}`),
      };
    });

  private handleErrors(httpErrorResponse: HttpErrorResponse): void {
    if (this.isErrorResponse(httpErrorResponse)) {
      console.log(true);
      console.log(httpErrorResponse as HttpErrorResponse);
    } else {
      console.log(httpErrorResponse);
    }
  }
}
