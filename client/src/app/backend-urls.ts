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
  },
};

export default BACKEND_URLS;
