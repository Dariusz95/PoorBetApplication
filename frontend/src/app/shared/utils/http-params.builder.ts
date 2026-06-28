import { HttpParams } from '@angular/common/http';
import { PageRequest } from '@shared/interfaces/page-request';

export function buildParams(
  page: PageRequest,
  filter: Record<string, unknown>,
): HttpParams {
  let params = new HttpParams().set('page', page.page).set('size', page.size);

  if (page.sort) {
    params = params.set('sort', `${page.sort},${page.direction ?? 'asc'}`);
  }

  Object.entries(filter).forEach(([key, value]) => {
    if (value === null || value === undefined) return;

    if (Array.isArray(value)) {
      params = params.set(key, value.map((v) => String(v)).join(','));
      return;
    }

    params = params.set(key, String(value));
  });

  return params;
}
