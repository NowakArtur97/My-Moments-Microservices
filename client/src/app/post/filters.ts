import Filter from './models/filter.model';

class DummyFilter implements Filter {
  name = 'dummy';

  apply(): void {}
}
const allFilters = [
  new DummyFilter(),
  new DummyFilter(),
  new DummyFilter(),
  new DummyFilter(),
  new DummyFilter(),
  new DummyFilter(),
  new DummyFilter(),
  new DummyFilter(),
  new DummyFilter(),
  new DummyFilter(),
  new DummyFilter(),
  new DummyFilter(),
  new DummyFilter(),
  new DummyFilter(),
  new DummyFilter(),
  new DummyFilter(),
  new DummyFilter(),
  new DummyFilter(),
  new DummyFilter(),
  new DummyFilter(),
  new DummyFilter(),
  new DummyFilter(),
];

export default allFilters;
