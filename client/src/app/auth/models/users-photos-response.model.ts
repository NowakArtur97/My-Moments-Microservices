interface UserPhotoModel {
  readonly username: string;
  readonly image: string;
}

interface UsersPhotosResponse {
  readonly usersPhotos: UserPhotoModel[];
}

export { UsersPhotosResponse, UserPhotoModel };
