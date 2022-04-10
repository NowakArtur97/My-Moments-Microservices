import EditorSlider from '../models/editor-slider.model';

const allEditorSliders: EditorSlider[] = [
  new EditorSlider('brightness', 0, 100, 50),
  new EditorSlider('contrast', 0, 500, 100),
  new EditorSlider('blur', 0, 500, 0, 'px'),
  new EditorSlider('grayscale', 0, 100, 100),
];

export default allEditorSliders;
