import EditorFilter from '../models/editor-slider.model';
import Filter from '../models/filter.model';
import ImageSnippet from '../models/image-snippet.model';
import ALL_EDITOR_SLIDERS from './editor.sliders.data';

class EmptyFilter extends Filter {
  constructor(readonly name: string) {
    super(name, 'none');
  }
  apply(imageSnippet: ImageSnippet): void {
    imageSnippet.editorSliders = ALL_EDITOR_SLIDERS.map((slider) => {
      return { ...slider } as EditorFilter;
    });
  }
}
const blurFilter = new Filter('blur', 'blur(5px)', 5);

const ALL_EDITOR_FILTERS: Filter[] = [new EmptyFilter('original'), blurFilter];

export default ALL_EDITOR_FILTERS;
