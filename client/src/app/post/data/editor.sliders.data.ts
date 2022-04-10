import EditorSlider from '../models/editor-slider.model';

const allEditorSliders: EditorSlider[] = [
  new EditorSlider('brightness', 0, 100, 100),
  new EditorSlider('contrast', 0, 500, 100),
  new EditorSlider('blur', 0, 10, 0, 'px'),
  new EditorSlider('grayscale', 0, 100, 0),
];

export default allEditorSliders;
