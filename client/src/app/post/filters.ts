import Filter from './models/filter.model';

class DummyFilter implements Filter {
  name = 'dummy';

  apply(): void {}
}
const filters = [new DummyFilter()];

export default filters;
