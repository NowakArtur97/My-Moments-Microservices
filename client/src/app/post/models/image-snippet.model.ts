import EditorFilter from './editor-slider.model';

export default interface ImageSnippet {
  readonly src: string;
  readonly file: File;
  readonly editorSliders: EditorFilter[];
}
