export default interface ErrorResponse {
  readonly errors: string[];
  readonly status: number;
  readonly timestamp: Date;
}
