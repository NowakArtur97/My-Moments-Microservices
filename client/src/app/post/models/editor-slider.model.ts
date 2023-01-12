export default class EditorFilter {
  constructor(
    readonly name: string,
    readonly minValue: number,
    readonly maxValue: number,
    readonly defaultValue: number,
    readonly unit: string = '%'
  ) {}
  public currentValue: number = this.defaultValue;
}
