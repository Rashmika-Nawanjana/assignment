export type SortDir = 'asc' | 'desc';

export function compareValues(a: unknown, b: unknown): number {
  if (a == null && b == null) {
    return 0;
  }
  if (a == null) {
    return -1;
  }
  if (b == null) {
    return 1;
  }

  if (typeof a === 'number' && typeof b === 'number') {
    return a - b;
  }

  const aNum = typeof a === 'string' && a.trim() !== '' && !Number.isNaN(Number(a)) ? Number(a) : null;
  const bNum = typeof b === 'string' && b.trim() !== '' && !Number.isNaN(Number(b)) ? Number(b) : null;
  if (aNum != null && bNum != null) {
    return aNum - bNum;
  }

  return String(a).localeCompare(String(b), undefined, { sensitivity: 'base', numeric: true });
}

export function sortRows<T>(
  rows: T[],
  getter: (row: T) => unknown,
  dir: SortDir
): T[] {
  const factor = dir === 'asc' ? 1 : -1;
  return [...rows].sort((left, right) => factor * compareValues(getter(left), getter(right)));
}

export function filterRows<T>(
  rows: T[],
  query: string,
  getters: Array<(row: T) => unknown>
): T[] {
  const needle = query.trim().toLowerCase();
  if (!needle) {
    return rows;
  }

  return rows.filter((row) =>
    getters.some((getter) => String(getter(row) ?? '').toLowerCase().includes(needle))
  );
}

export function toggleSort(
  currentKey: string,
  currentDir: SortDir,
  nextKey: string
): { key: string; dir: SortDir } {
  if (currentKey === nextKey) {
    return { key: nextKey, dir: currentDir === 'asc' ? 'desc' : 'asc' };
  }
  return { key: nextKey, dir: 'asc' };
}
