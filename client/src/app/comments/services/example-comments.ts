import Comment from '../models/comment.model';

function randomDate(): Date {
  const start = new Date(2022, 0, 1);
  const end = new Date();
  return new Date(
    start.getTime() + Math.random() * (end.getTime() - start.getTime())
  );
}

const EXAMPLE_COMMENTS: Comment[] = [
  {
    id: '115b9b41-d03f-4642-9317-bf2b68155be6',
    content: 'Great!!!',
    author: 'user1',
    createDate: randomDate(),
    modifyDate: randomDate(),
  },
  {
    id: 'f2b1cd85-4244-467f-b43b-20671de5e908',
    content: 'Great!!!',
    author: 'user1',
    createDate: randomDate(),
    modifyDate: randomDate(),
  },
  {
    id: '0325a27f-b8b0-4cf8-9e9e-7f1dedec8007',
    content: 'Great!!!',
    author: 'user1',
    createDate: randomDate(),
    modifyDate: randomDate(),
  },
  {
    id: 'e8230436-9d3a-49f5-a3ec-161ead7a5b93',
    content: 'Great!!!',
    author: 'user1',
    createDate: randomDate(),
    modifyDate: randomDate(),
  },
  {
    id: '210570cf-103e-4392-86fd-d9324db2e6ac',
    content: 'Great!!!',
    author: 'user1',
    createDate: randomDate(),
    modifyDate: randomDate(),
  },
  {
    id: 'd10b2846-b951-46c8-8bfe-c0ca16918912',
    content: 'Great!!!',
    author: 'user1',
    createDate: randomDate(),
    modifyDate: randomDate(),
  },
];

export default EXAMPLE_COMMENTS;
