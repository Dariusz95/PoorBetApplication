import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { RouteLink } from '@core/routing/route-link';
import { RoutePath } from '@core/routing/route-path';
import { getTranslocoModule } from '@shared/utils/get-transloco-module';
import { of, throwError } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { AuthService } from '../../services/auth.service';
import { RegisterPageComponent } from './register-page.component';

describe('RegisterPageComponent', () => {
  let component: RegisterPageComponent;
  let fixture: ComponentFixture<RegisterPageComponent>;
  let authService: { register: ReturnType<typeof vi.fn> };
  let router: Router;

  beforeEach(async () => {
    authService = { register: vi.fn() };

    await TestBed.configureTestingModule({
      imports: [RegisterPageComponent, getTranslocoModule()],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterPageComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component.RoutePath).toBeDefined();
    expect(component).toBeTruthy();
  });

  describe('onSubmitForm', () => {
    it('should call authService.register with the submitted credentials', () => {
      authService.register.mockReturnValue(of({ success: true }));

      component.onSubmitForm({
        email: 'a@b.pl',
        password: 'zaq1@WSX',
        confirmPassword: 'zaq1@WSX',
      });

      expect(authService.register).toHaveBeenCalledWith({
        email: 'a@b.pl',
        password: 'zaq1@WSX',
      });
    });

    it('should navigate to the login page on successful registration', () => {
      authService.register.mockReturnValue(of({ success: true }));

      component.onSubmitForm({
        email: 'a@b.pl',
        password: 'zaq1@WSX',
        confirmPassword: 'zaq1@WSX',
      });

      expect(router.navigate).toHaveBeenCalledWith(RouteLink[RoutePath.Login]);
    });

    it('should reset submitted to false after the request completes', () => {
      authService.register.mockReturnValue(of({ success: true }));

      component.onSubmitForm({
        email: 'a@b.pl',
        password: 'zaq1@WSX',
        confirmPassword: 'zaq1@WSX',
      });

      expect(component.submitted()).toBe(false);
    });

    it('should not navigate and should reset submitted on registration error', () => {
      authService.register.mockReturnValue(
        throwError(() => new Error('email already taken')),
      );

      component.onSubmitForm({
        email: 'a@b.pl',
        password: 'zaq1@WSX',
        confirmPassword: 'zaq1@WSX',
      });

      expect(router.navigate).not.toHaveBeenCalled();
      expect(component.submitted()).toBe(false);
    });
  });
});
