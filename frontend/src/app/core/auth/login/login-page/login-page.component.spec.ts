import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { getTranslocoModule } from '@shared/utils/get-transloco-module';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { of, throwError } from 'rxjs';
import { RouteLink } from '@core/routing/route-link';
import { RoutePath } from '@core/routing/route-path';
import { LoginResponse } from '../../responses/login-response';
import { AuthService } from '../../services/auth.service';
import { LoginPageComponent } from './login-page.component';

describe('LoginPageComponent', () => {
  let component: LoginPageComponent;
  let fixture: ComponentFixture<LoginPageComponent>;
  let authService: { login: ReturnType<typeof vi.fn> };
  let router: Router;

  beforeEach(async () => {
    authService = { login: vi.fn() };

    await TestBed.configureTestingModule({
      imports: [LoginPageComponent, getTranslocoModule()],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginPageComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('onSubmitForm', () => {
    const loginResponse: LoginResponse = {
      tokenType: 'Bearer',
      username: 'testuser',
      roles: ['USER'],
      expiresAt: 0,
      token: 'mock-jwt-token',
    };

    it('should call authService.login with the submitted credentials', () => {
      authService.login.mockReturnValue(of(loginResponse));

      component.onSubmitForm({ email: 'a@b.pl', password: 'zaq1@WSX' });

      expect(authService.login).toHaveBeenCalledWith({
        email: 'a@b.pl',
        password: 'zaq1@WSX',
      });
    });

    it('should navigate to the app on successful login', () => {
      authService.login.mockReturnValue(of(loginResponse));

      component.onSubmitForm({ email: 'a@b.pl', password: 'zaq1@WSX' });

      expect(router.navigate).toHaveBeenCalledWith(RouteLink[RoutePath.App]);
    });

    it('should set submitted to true while the request is in flight and back to false afterwards', () => {
      authService.login.mockReturnValue(of(loginResponse));

      expect(component.submitted()).toBe(false);
      component.onSubmitForm({ email: 'a@b.pl', password: 'zaq1@WSX' });
      expect(component.submitted()).toBe(false);
    });

    it('should not navigate and should reset submitted on login error', () => {
      authService.login.mockReturnValue(
        throwError(() => new Error('invalid credentials')),
      );

      component.onSubmitForm({ email: 'a@b.pl', password: 'wrong' });

      expect(router.navigate).not.toHaveBeenCalled();
      expect(component.submitted()).toBe(false);
    });
  });
});
