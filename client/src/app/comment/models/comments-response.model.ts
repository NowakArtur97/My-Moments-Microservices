import Comment from './comment.model';

export default interface CommentsResponse {
  readonly comments: Comment[];
}
