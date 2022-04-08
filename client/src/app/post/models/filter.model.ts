export default interface Filter {
  readonly name: string;
  apply(): void;
}
