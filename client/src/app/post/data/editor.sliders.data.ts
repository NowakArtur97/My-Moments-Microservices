import EditorFilter from '../models/editor-slider.model';

const allEditorSliders: EditorFilter[] = [
  new EditorFilter('brightness', 0, 100, 100),
  new EditorFilter('contrast', 0, 500, 100),
  new EditorFilter('blur', 0, 10, 0, 'px'),
  new EditorFilter('grayscale', 0, 100, 0),
];

export default allEditorSliders;
