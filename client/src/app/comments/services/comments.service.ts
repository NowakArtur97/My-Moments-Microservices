import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import BACKEND_URLS from 'src/app/backend-urls';
import HttpService from 'src/app/common/services/http.service';
import { environment } from 'src/environments/environment.local';

import Comment from '../models/comment.model';
import CommentsResponse from '../models/comments-response.model';
import EXAMPLE_COMMENTS from './example-comments';

@Injectable({ providedIn: 'root' })
export class CommentService extends HttpService {
  comments = new BehaviorSubject<Comment[]>([]);
  // TODO: DELETE
  // comments = new BehaviorSubject<Comment[]>(EXAMPLE_COMMENTS);

  constructor(protected httpClient: HttpClient) {
    super(httpClient);
  }

  getComments(postId: string): void {
    this.httpClient
      .get<CommentsResponse>(
        `${environment.commentsServiceUrl}${BACKEND_URLS.comments.postComments(
          postId
        )}`
      )
      .subscribe(
        (commentsResponse: CommentsResponse) =>
          this.comments.next(commentsResponse.comments),
        (httpErrorResponse: HttpErrorResponse) => {
          // console.log(httpErrorResponse);
          // TODO: DELETE
          this.comments.next(EXAMPLE_COMMENTS);
          // this.comments.next([]);
        }
      );
  }
}
