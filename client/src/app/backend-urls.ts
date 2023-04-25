const BACKEND_URLS = {
  user: {
    authentication: '/authentication',
    registration: '/registration/register',
  },
  common: {
    myResource: '/me',
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
