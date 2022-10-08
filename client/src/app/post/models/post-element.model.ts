import Post from './post.model';

export default interface PostElement extends Post {
  isActive: boolean;
  offsetLeft: number;
}
