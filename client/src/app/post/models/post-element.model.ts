import Post from './post.model';

export default interface PostElement extends Post {
  currentPhotoIndex: number;
  isActive: boolean;
  isCurrentlyLastElement: boolean;
}
