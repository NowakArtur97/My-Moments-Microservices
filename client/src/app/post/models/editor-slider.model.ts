export default abstract class EditorSlider {
  constructor(
    readonly name: string,
    readonly minValue: number,
    readonly maxValue: number,
    readonly defaultValue: number,
    public currentValue: number = defaultValue
  ) {}

  abstract apply(context: CanvasRenderingContext2D, value: number): void;
}
