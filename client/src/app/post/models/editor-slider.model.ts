export default class EditorSlider {
  constructor(
    readonly name: string,
    readonly minValue: number,
    readonly maxValue: number,
    readonly defaultValue: number,
    readonly unit: string = '%'
  ) {}
  public currentValue: number = this.defaultValue;

  apply(value: number, currentFilters: Map<string, string>) {
    this.currentValue = value;
    currentFilters.set(
      this.name,
      `${this.name}(${this.currentValue}${this.unit})`
    );
  }
}
