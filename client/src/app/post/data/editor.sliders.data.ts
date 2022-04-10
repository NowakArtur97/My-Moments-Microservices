import EditorSlider from '../models/editor-slider.model';

class BrithnessSlider extends EditorSlider {
  constructor(
    readonly name: string,
    readonly minValue: number,
    readonly maxValue: number,
    readonly defaultValue: number,
    public currentValue: number = defaultValue
  ) {
    super(name, minValue, maxValue, defaultValue);
  }

  apply(context: CanvasRenderingContext2D, value: number): void {
    this.currentValue = value;
    context.filter = `${this.name}(${value}%)`;
  }
}

const allEditorSliders: EditorSlider[] = [
  new BrithnessSlider('brightness', 0, 100, 50),
];

export default allEditorSliders;
