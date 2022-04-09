export default interface Filter {
  readonly name: string;
  apply(context: CanvasRenderingContext2D): void;
}
