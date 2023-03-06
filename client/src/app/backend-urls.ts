const BACKEND_URLS = {
  user: {
    authentication: '/authentication',
    registration: '/registration/register',
  },
  common: {
    myResource: '/me',
  },
  comments: {
    postComments: (postId: string): string => `/${postId}/comments`,
    postComment: (postId: string, commentId: string): string =>
      `/${postId}/comments/${commentId}`,
  },
};

export default BACKEND_URLS;
