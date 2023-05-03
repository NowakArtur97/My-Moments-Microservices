const BACKEND_URLS = {
  user: {
    users: '/users',
    authentication: '/authentication',
    registration: '/registration/register',
  },
  common: {
    myResource: '/me',
  },
  post: {
    usersPosts: (usernames: string[]): string => `?usernames=${usernames}`,
  },
  comment: {
    postComments: (postId: string): string => `/${postId}/comments`,
    postComment: (postId: string, commentId: string): string =>
      `/${postId}/comments/${commentId}`,
  },
  follower: {
    followers: (username: string): string => `/followers/${username}`,
    following: (username: string): string => `/following/${username}`,
  },
};

export default BACKEND_URLS;
