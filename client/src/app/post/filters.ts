import Filter from './models/filter.model';

class EmptyFilter implements Filter {
  name = 'oryginal';

  apply(context: CanvasRenderingContext2D): void {
    context.filter = 'none';
  }
}

class SaturateFilter implements Filter {
  name = 'saturate';

  apply(context: CanvasRenderingContext2D): void {
    context.filter = `${this.name}(8)`;
  }
}

const allFilters = [new EmptyFilter(), new SaturateFilter()];

export default allFilters;
