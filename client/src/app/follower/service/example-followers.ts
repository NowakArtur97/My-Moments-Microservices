import EXAMPLE_PHOTO from 'src/app/auth/services/example-photo';

import UserAcquaintance from '../models/user-acquaintance.model';

const MIN_FOLLOWERS = 5;
const MAX_FOLLOWERS = 2000;

const getRandomNumberOfUsers = (): number =>
  Math.floor(Math.random() * (MAX_FOLLOWERS - MIN_FOLLOWERS + 1)) +
  MIN_FOLLOWERS;

const EXAMPLE_FOLLOWERS: UserAcquaintance[] = [
  {
    username: 'follower1',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
    photo: `data:image/jpg;base64,${EXAMPLE_PHOTO}`,
  },
  {
    username: 'follower2',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
    photo: `data:image/jpg;base64,${EXAMPLE_PHOTO}`,
  },
  {
    username: 'follower3',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
    photo: `data:image/jpg;base64,${EXAMPLE_PHOTO}`,
  },
  {
    username: 'follower4',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
    photo: `data:image/jpg;base64,${EXAMPLE_PHOTO}`,
  },
  {
    username: 'follower5',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
    photo: `data:image/jpg;base64,${EXAMPLE_PHOTO}`,
  },
  {
    username: 'follower6',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
    photo: `data:image/jpg;base64,${EXAMPLE_PHOTO}`,
  },
  {
    username: 'follower7',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
    photo: `data:image/jpg;base64,${EXAMPLE_PHOTO}`,
  },
  {
    username: 'follower8',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
    photo: `data:image/jpg;base64,${EXAMPLE_PHOTO}`,
  },
  {
    username: 'follower9',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
    photo: `data:image/jpg;base64,${EXAMPLE_PHOTO}`,
  },
  {
    username: 'follower10',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
    photo: `data:image/jpg;base64,${EXAMPLE_PHOTO}`,
  },
];

const EXAMPLE_FOLLOWERS_2: UserAcquaintance[] = [
  {
    username: 'follower1',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
    photo: `data:image/jpg;base64,${EXAMPLE_PHOTO}`,
  },
  {
    username: 'follower11',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
    photo: `data:image/jpg;base64,${EXAMPLE_PHOTO}`,
  },
  {
    username: 'follower12',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
    photo: `data:image/jpg;base64,${EXAMPLE_PHOTO}`,
  },
  {
    username: 'follower13',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
    photo: `data:image/jpg;base64,${EXAMPLE_PHOTO}`,
  },
  {
    username: 'follower14',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
    photo: `data:image/jpg;base64,${EXAMPLE_PHOTO}`,
  },
  {
    username: 'follower15',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
    photo: `data:image/jpg;base64,${EXAMPLE_PHOTO}`,
  },
  {
    username: 'follower16',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
    photo: `data:image/jpg;base64,${EXAMPLE_PHOTO}`,
  },
  {
    username: 'follower17',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
    photo: `data:image/jpg;base64,${EXAMPLE_PHOTO}`,
  },
  {
    username: 'follower18',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
    photo: `data:image/jpg;base64,${EXAMPLE_PHOTO}`,
  },
  {
    username: 'follower19',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
    photo: `data:image/jpg;base64,${EXAMPLE_PHOTO}`,
  },
  {
    username: 'follower20',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
    photo: `data:image/jpg;base64,${EXAMPLE_PHOTO}`,
  },
];

export { EXAMPLE_FOLLOWERS, EXAMPLE_FOLLOWERS_2 };
