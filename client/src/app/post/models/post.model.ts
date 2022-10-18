export default interface Post {
  readonly id: string;
  readonly caption: string;
  readonly author: string;
  readonly photos: string[];
  currentPhotoIndex: number;
}
