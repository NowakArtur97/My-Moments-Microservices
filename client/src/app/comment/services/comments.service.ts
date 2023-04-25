import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import BACKEND_URLS from 'src/app/backend-urls';
import HttpService from 'src/app/common/services/http.service';
import { environment } from 'src/environments/environment.local';

import CommentDTO from '../models/comment.dto';
import Comment from '../models/comment.model';
import CommentsResponse from '../models/comments-response.model';
import EXAMPLE_COMMENTS from './example-comments';

@Injectable({ providedIn: 'root' })
export class CommentService extends HttpService {
  // comments = new BehaviorSubject<Comment[]>([]);
  // TODO: DELETE
  private DEFAULT_COMMENT = {
    id: '',
    author: '',
    content: '',
    createDate: new Date(),
    modifyDate: new Date(),
  };
  comments = new BehaviorSubject<Comment[]>(EXAMPLE_COMMENTS);
  editComment = new BehaviorSubject<Comment>(this.DEFAULT_COMMENT);

  constructor(protected httpClient: HttpClient) {
    super(httpClient);
  }

  getComments(postId: string): void {
    this.httpClient
      .get<CommentsResponse>(
        `${environment.commentServiceUrl}${BACKEND_URLS.comment.postComments(
          postId
        )}`
      )
      .subscribe(
        (commentsResponse: CommentsResponse) =>
          this.comments.next(commentsResponse.comments),
        (httpErrorResponse: HttpErrorResponse) => {
          // TODO: DELETE
          this.comments.next(EXAMPLE_COMMENTS);
          this.logErrors(httpErrorResponse);
          // this.comments.next([]);
        }
      );
  }

  addComment(postId: string, commentDTO: CommentDTO): void {
    this.httpClient
      .post<Comment>(
        `${environment.commentServiceUrl}${BACKEND_URLS.comment.postComments(
          postId
        )}`,
        commentDTO
      )
      .subscribe(
        (newComment: Comment) =>
          this.comments.next([...this.comments.getValue(), newComment]),
        (httpErrorResponse: HttpErrorResponse) =>
          this.logErrors(httpErrorResponse)
      );
  }

  deleteComment(postId: string, commentId: string): void {
    this.httpClient
      .delete(
        `${environment.commentServiceUrl}${BACKEND_URLS.comment.postComment(
          postId,
          commentId
        )}`
      )
      .subscribe(
        () => {},
        (httpErrorResponse: HttpErrorResponse) =>
          this.logErrors(httpErrorResponse)
      );
  }

  startEditingComment(commentToEdit: Comment): void {
    this.editComment.next(commentToEdit);
  }

  stopEditingComment(): void {
    this.editComment.next(this.DEFAULT_COMMENT);
  }

  hideComment(commentId: string): void {
    this.comments.next([
      ...this.comments.getValue().filter((comment) => comment.id !== commentId),
    ]);
  }
}
