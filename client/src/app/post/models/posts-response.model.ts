import Post from './post.model';

export default interface PostsResponse {
  readonly posts: Post[];
}
