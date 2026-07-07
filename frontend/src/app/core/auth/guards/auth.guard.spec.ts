import { TestBed } from '@angular/core/testing';
import { Router, UrlTree } from '@angular/router';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { AuthService } from '../services/auth.service';
import { authGuard } from './auth.guard';

describe('authGuard', () => {
  let authService: { isLoggedIn: ReturnType<typeof vi.fn> };
  let router: { createUrlTree: ReturnType<typeof vi.fn> };

  beforeEach(() => {
    authService = { isLoggedIn: vi.fn() };
    router = { createUrlTree: vi.fn() };

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router },
      ],
    });
  });

  function runGuard() {
    return TestBed.runInInjectionContext(() =>
      authGuard({} as any, {} as any),
    );
  }

  it('should allow activation when user is logged in', () => {
    authService.isLoggedIn.mockReturnValue(true);

    const result = runGuard();

    expect(result).toBe(true);
    expect(router.createUrlTree).not.toHaveBeenCalled();
  });

  it('should redirect to /auth when user is not logged in', () => {
    authService.isLoggedIn.mockReturnValue(false);
    const urlTree = {} as UrlTree;
    router.createUrlTree.mockReturnValue(urlTree);

    const result = runGuard();

    expect(router.createUrlTree).toHaveBeenCalledWith(['/', 'auth']);
    expect(result).toBe(urlTree);
  });
});
