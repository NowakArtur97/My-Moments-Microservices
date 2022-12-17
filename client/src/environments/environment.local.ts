const baseUrl = 'http://192.168.99.100:';
const localhostUrl = 'http://localhost:';

export const environment = {
  userServiceUrl: `${baseUrl}8081/api/v1`,
  postServiceUrl: `${localhostUrl}8082/api/v1/posts`,
  commentsServiceUrl: `${baseUrl}8083/api/v1/posts`,
};
