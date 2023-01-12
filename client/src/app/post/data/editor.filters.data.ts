import Filter from '../models/filter.model';

const ALL_EDITOR_FILTERS: Filter[] = [
  new Filter('oryginal'),
  new Filter('blur', 'blur(5px)'),
  new Filter('drop-shadow', 'drop-shadow(16px 16px 20px blue)'),
  new Filter('multiple', 'contrast(175%) brightness(50%)'),
];

export default ALL_EDITOR_FILTERS;
