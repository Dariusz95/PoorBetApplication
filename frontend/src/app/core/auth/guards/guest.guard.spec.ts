import { TestBed } from '@angular/core/testing';
import { Router, UrlTree } from '@angular/router';
import { RouteLink } from '@core/routing/route-link';
import { RoutePath } from '@core/routing/route-path';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { AuthService } from '../services/auth.service';
import { guestGuard } from './guest.guard';

describe('guestGuard', () => {
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
      guestGuard({} as any, {} as any),
    );
  }

  it('should allow activation when user is not logged in', () => {
    authService.isLoggedIn.mockReturnValue(false);

    const result = runGuard();

    expect(result).toBe(true);
    expect(router.createUrlTree).not.toHaveBeenCalled();
  });

  it('should redirect to the app when user is already logged in', () => {
    authService.isLoggedIn.mockReturnValue(true);
    const urlTree = {} as UrlTree;
    router.createUrlTree.mockReturnValue(urlTree);

    const result = runGuard();

    expect(router.createUrlTree).toHaveBeenCalledWith(
      RouteLink[RoutePath.App],
    );
    expect(result).toBe(urlTree);
  });
});
