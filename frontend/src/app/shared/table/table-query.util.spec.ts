import { filterRows, sortRows, toggleSort } from './table-query.util';

describe('table-query.util', () => {
  const rows = [
    { name: 'Beta', price: 30 },
    { name: 'Alpha', price: 10 },
    { name: 'Gamma', price: 20 }
  ];

  it('sorts ascending and descending by price', () => {
    expect(sortRows(rows, (row) => row.price, 'asc').map((row) => row.price)).toEqual([10, 20, 30]);
    expect(sortRows(rows, (row) => row.price, 'desc').map((row) => row.price)).toEqual([30, 20, 10]);
  });

  it('filters by query across getters', () => {
    const filtered = filterRows(rows, 'alp', [(row) => row.name]);
    expect(filtered.length).toBe(1);
    expect(filtered[0].name).toBe('Alpha');
  });

  it('toggles sort direction on the same column', () => {
    expect(toggleSort('price', 'asc', 'price')).toEqual({ key: 'price', dir: 'desc' });
    expect(toggleSort('price', 'asc', 'name')).toEqual({ key: 'name', dir: 'asc' });
  });
});
