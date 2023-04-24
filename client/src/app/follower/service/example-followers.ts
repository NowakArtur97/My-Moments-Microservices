import Follower from '../models/follower.model';

const MIN_FOLLOWERS = 5;
const MAX_FOLLOWERS = 2000;

const getRandomNumberOfUsers = (): number =>
  Math.floor(Math.random() * (MAX_FOLLOWERS - MIN_FOLLOWERS + 1)) +
  MIN_FOLLOWERS;

const EXAMPLE_FOLLOWERS: Follower[] = [
  {
    username: 'follower1',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
  },
  {
    username: 'follower2',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
  },
  {
    username: 'follower3',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
  },
  {
    username: 'follower4',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
  },
  {
    username: 'follower5',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
  },
  {
    username: 'follower6',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
  },
  {
    username: 'follower7',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
  },
  {
    username: 'follower8',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
  },
  {
    username: 'follower9',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
  },
  {
    username: 'follower10',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
  },
  {
    username: 'follower11',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
  },
  {
    username: 'follower12',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
  },
  {
    username: 'follower13',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
  },
  {
    username: 'follower14',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
  },
  {
    username: 'follower15',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
  },
  {
    username: 'follower16',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
  },
  {
    username: 'follower17',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
  },
  {
    username: 'follower18',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
  },
  {
    username: 'follower19',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
  },
  {
    username: 'follower20',
    numberOfFollowers: getRandomNumberOfUsers(),
    numberOfFollowing: getRandomNumberOfUsers(),
  },
];

export default EXAMPLE_FOLLOWERS;
