import PostState from './post-state.enum';
import Post from './post.model';

export default interface PostElement extends Post {
  currentPhotoIndex: number;
  state: PostState;
  stoppedBeingActive: boolean;
  isCurrentlyLastElement: boolean;
  authorImage: string;
}
