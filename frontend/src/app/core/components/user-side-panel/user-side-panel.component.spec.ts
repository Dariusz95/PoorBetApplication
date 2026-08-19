import { DialogRef } from '@angular/cdk/dialog';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { AccountService } from '@core/account/services/account.service';
import { AuthService } from '@core/auth/services/auth.service';
import { JwtAuthStateService } from '@core/auth/services/jwt-auth-state.service';
import { RouteLink } from '@core/routing/route-link';
import { RoutePath } from '@core/routing/route-path';
import { RoutingService } from '@core/routing/routing.service';
import { getTranslocoModule } from '@shared/utils/get-transloco-module';
import { Observable, of } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { LoginResponse } from '../../auth/responses/login-response';
import { UserSidePanelComponent } from './user-side-panel.component';

describe('UserSidePanelComponent', () => {
  let component: UserSidePanelComponent;
  let fixture: ComponentFixture<UserSidePanelComponent>;
  let authService: {
    isLoggedIn$: Observable<boolean>;
    loginAsTestUser: ReturnType<typeof vi.fn>;
  };
  let dialogRef: { close: ReturnType<typeof vi.fn> };
  let router: Router;

  beforeEach(async () => {
    authService = {
      isLoggedIn$: of(false),
      loginAsTestUser: vi.fn(),
    };
    dialogRef = { close: vi.fn() };

    await TestBed.configureTestingModule({
      imports: [UserSidePanelComponent, getTranslocoModule()],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authService },
        { provide: DialogRef, useValue: dialogRef },
        {
          provide: JwtAuthStateService,
          useValue: { getSubject: () => null },
        },
        {
          provide: AccountService,
          useValue: {
            balance: () => 0,
            loading: () => false,
            ensureAccountStateLoaded: vi.fn(),
          },
        },
        {
          provide: RoutingService,
          useValue: { navigateTo: vi.fn() },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UserSidePanelComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('loginAsTestUser', () => {
    const loginResponse: LoginResponse = {
      tokenType: 'Bearer',
      username: 'testuser',
      roles: ['USER'],
      expiresAt: 0,
      token: 'mock-jwt-token',
      refreshToken: 'mock-refresh-token',
    };

    it('should call authService.loginAsTestUser', () => {
      authService.loginAsTestUser.mockReturnValue(of(loginResponse));

      component.loginAsTestUser();

      expect(authService.loginAsTestUser).toHaveBeenCalled();
    });

    it('should navigate to the app and close the panel on success', () => {
      authService.loginAsTestUser.mockReturnValue(of(loginResponse));

      component.loginAsTestUser();

      expect(router.navigate).toHaveBeenCalledWith(RouteLink[RoutePath.App]);
      expect(dialogRef.close).toHaveBeenCalled();
    });

    it('should reset submittingTestUser back to false after the request completes', () => {
      authService.loginAsTestUser.mockReturnValue(of(loginResponse));

      component.loginAsTestUser();

      expect(component.submittingTestUser()).toBe(false);
    });
  });
});
