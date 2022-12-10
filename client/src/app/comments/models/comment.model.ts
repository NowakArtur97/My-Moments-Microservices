export default interface Comment {
  readonly id: string;
  readonly content: string;
  readonly author: string;
  readonly createDate: Date;
  readonly modifyDate: Date;
}
