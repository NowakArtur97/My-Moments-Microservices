import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import HttpService from 'src/app/common/services/http.service';

import Comment from '../models/comment.model';
import EXAMPLE_COMMENTS from './example-comments';

@Injectable({ providedIn: 'root' })
export class CommentService extends HttpService {
  //   comments = new BehaviorSubject<Comment[]>();
  // TODO: CommentService: DELETE
  comments = new BehaviorSubject<Comment[]>(EXAMPLE_COMMENTS);

  constructor(protected httpClient: HttpClient, private router: Router) {
    super(httpClient);
  }
}
