import { describe, expect, it } from 'vitest';
import { buildParams } from './http-params.builder';

describe('buildParams', () => {
  it('should always include page and size', () => {
    const params = buildParams({ page: 2, size: 10 }, {});

    expect(params.get('page')).toBe('2');
    expect(params.get('size')).toBe('10');
  });

  it('should combine sort and direction when sort is provided', () => {
    const params = buildParams(
      { page: 0, size: 20, sort: 'createdAt', direction: 'desc' },
      {},
    );

    expect(params.get('sort')).toBe('createdAt,desc');
  });

  it('should default direction to asc when not provided', () => {
    const params = buildParams({ page: 0, size: 20, sort: 'createdAt' }, {});

    expect(params.get('sort')).toBe('createdAt,asc');
  });

  it('should omit sort when not provided', () => {
    const params = buildParams({ page: 0, size: 20 }, {});

    expect(params.has('sort')).toBe(false);
  });

  it('should add filter values as strings', () => {
    const params = buildParams({ page: 0, size: 20 }, { status: 'OPEN' });

    expect(params.get('status')).toBe('OPEN');
  });

  it('should join array filter values with a comma', () => {
    const params = buildParams(
      { page: 0, size: 20 },
      { statuses: ['WON', 'LOST'] },
    );

    expect(params.get('statuses')).toBe('WON,LOST');
  });

  it('should skip null and undefined filter values', () => {
    const params = buildParams(
      { page: 0, size: 20 },
      { status: null, other: undefined },
    );

    expect(params.has('status')).toBe(false);
    expect(params.has('other')).toBe(false);
  });

  it('should stringify non-string filter values', () => {
    const params = buildParams({ page: 0, size: 20 }, { minAmount: 15 });

    expect(params.get('minAmount')).toBe('15');
  });
});
