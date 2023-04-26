import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import BACKEND_URLS from 'src/app/backend-urls';
import { APP_ROUTES } from 'src/app/common/const.data';
import HttpService from 'src/app/common/services/http.service';
import { environment } from 'src/environments/environment.local';

import PostElement from '../models/post-element.model';
import PostState from '../models/post-state.enum';
import PostDTO from '../models/post.dto';
import Post from '../models/post.model';
import PostsResponse from '../models/posts-response.model';
import EXAMPLE_POSTS from './example-posts';

@Injectable({ providedIn: 'root' })
export class PostService extends HttpService {
  // myPosts = new BehaviorSubject<Post[]>([]);
  // TODO: DELETE
  myPosts = new BehaviorSubject<Post[]>(
    this.mapBinaryToJpgs(
      [...EXAMPLE_POSTS].map((post) => {
        return { ...post, photos: this.shuffleArray([...post.photos]) };
      })
    ).map((post) => {
      return { ...post, currentPhotoIndex: 0 };
    })
  );
  editedPost = new BehaviorSubject<Post | null>(null);

  constructor(protected httpClient: HttpClient, private router: Router) {
    super(httpClient, environment.postServiceUrl);
  }

  createPost(postDTO: PostDTO): void {
    const { files, caption } = postDTO;
    const multipartData = this.createFormDataFromFiles(
      [{ key: 'photos', files }],
      [{ key: 'post', value: { caption } }]
    );
    this.httpClient.post<Post>(`${this.baseUrl}`, multipartData).subscribe(
      (newPost: Post) => this.handleSuccessfullPostsResponse([newPost]),
      (httpErrorResponse: HttpErrorResponse) =>
        this.logErrors(httpErrorResponse)
    );
  }

  getMyPosts(): void {
    this.httpClient
      .get<PostsResponse>(`${this.baseUrl}${BACKEND_URLS.common.myResource}`)
      .subscribe(
        ({ posts }: PostsResponse) =>
          this.handleSuccessfullPostsResponse(posts),
        (httpErrorResponse: HttpErrorResponse) => {
          this.logErrors(httpErrorResponse);
          // TODO: DELETE
          const mockPosts: Post[] = [...EXAMPLE_POSTS].map((post) => {
            return { ...post, photos: this.shuffleArray([...post.photos]) };
          });
          this.handleSuccessfullPostsResponse(mockPosts);
        }
      );
  }

  startEditingPost = (post: Post): void => this.editedPost.next(post);

  editPost(postId: string, postDTO: PostDTO): void {
    const { files, caption } = postDTO;
    const multipartData = this.createFormDataFromFiles(
      [{ key: 'photos', files }],
      [{ key: 'post', value: { caption } }]
    );
    this.httpClient
      .put<Post>(`${this.baseUrl}/${postId}`, multipartData)
      .subscribe(
        (newPost: Post) => this.handleSuccessfullPostsResponse([newPost]),
        (httpErrorResponse: HttpErrorResponse) =>
          this.logErrors(httpErrorResponse)
      );
  }

  deletePost(postId: string) {
    this.httpClient.delete(`${this.baseUrl}/${postId}`).subscribe(
      () => {},
      (httpErrorResponse: HttpErrorResponse) =>
        this.logErrors(httpErrorResponse)
    );
  }

  hidePost(postId: string): void {
    this.myPosts.next([
      ...this.myPosts.getValue().filter((post) => post.id !== postId),
    ]);
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

  // TODO: make private and revert changes
  mapBinaryToJpgs(posts: Post[]): Post[] {
    return posts.map((post: Post) => {
      return {
        ...post,
        photos: post.photos.map((photo) => this.mapToBase64(photo)),
      };
    });
  }

  // TODO: DELETE
  private shuffleArray(array: any[]): any[] {
    for (let i = array.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      const temp = array[i];
      array[i] = array[j];
      array[j] = temp;
    }
    return array;
  }
}
