// TODO: Refactor to abstract class if apply methods doesnt change in implmentations (?)
export default interface Filter {
  readonly name: string;
  apply(context: CanvasRenderingContext2D): void;
}
