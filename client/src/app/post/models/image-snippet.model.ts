export default interface ImageSnippet {
  readonly src: string;
  readonly file: File;
  readonly contextFilters: Map<string, string>;
}
