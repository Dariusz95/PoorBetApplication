import { HttpErrorResponse } from '@angular/common/http';
import { describe, expect, it } from 'vitest';
import { extractApiError } from './extract-api-error.util';

describe('extractApiError', () => {
  it('should return the parsed body when it matches the ApiError shape', () => {
    const error = new HttpErrorResponse({
      status: 400,
      error: {
        code: 'INSUFFICIENT_FUNDS',
        message: 'Not enough funds',
        timestamp: '2026-07-18T12:00:00Z',
        path: '/api/coupons',
      },
    });

    const result = extractApiError(error);

    expect(result?.code).toBe('INSUFFICIENT_FUNDS');
    expect(result?.message).toBe('Not enough funds');
  });

  it('should return null for a network error with no body', () => {
    const error = new HttpErrorResponse({ status: 0, error: null });

    expect(extractApiError(error)).toBeNull();
  });

  it('should return null when the body does not match the ApiError shape', () => {
    const error = new HttpErrorResponse({
      status: 500,
      error: '<html>Whitelabel Error Page</html>',
    });

    expect(extractApiError(error)).toBeNull();
  });

  it('should return null when required fields are missing', () => {
    const error = new HttpErrorResponse({
      status: 400,
      error: { message: 'Missing code field' },
    });

    expect(extractApiError(error)).toBeNull();
  });
});
