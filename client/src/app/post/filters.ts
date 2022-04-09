import Filter from './models/filter.model';

class DummyFilter implements Filter {
  name = 'dummy';

  apply(context: CanvasRenderingContext2D): void {}
}

class SaturateFilter implements Filter {
  name = 'saturate';

  apply(context: CanvasRenderingContext2D): void {
    context.filter = `${this.name}(8)`;
  }
}

const allFilters = [new DummyFilter(), new SaturateFilter()];

export default allFilters;
