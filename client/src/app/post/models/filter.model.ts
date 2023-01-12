import ImageSnippet from './image-snippet.model';

export default class Filter {
  constructor(
    readonly name: string,
    readonly filterValue: string,
    readonly value: number | null = null
  ) {}

  apply(imageSnippet: ImageSnippet): void {
    imageSnippet.editorSliders.find(
      (editorSlider) => editorSlider.name === this.name
    )!!.currentValue = this.value!!;
  }

  applyToContext(context: CanvasRenderingContext2D): void {
    context.filter = this.filterValue;
  }
}
