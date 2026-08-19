import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginFormComponent } from './login-form.component';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { getTranslocoModule } from '@shared/utils/get-transloco-module';

describe('LoginFormComponent', () => {
  let component: LoginFormComponent;
  let fixture: ComponentFixture<LoginFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginFormComponent, getTranslocoModule()],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('onSubmit', () => {
    it('should emit submitForm with the form values when the form is valid', () => {
      const emitSpy = vi.fn();
      component.submitForm.subscribe(emitSpy);

      component.form.setValue({
        email: 'user@example.com',
        password: 'zaq1@WSX',
      });
      component.onSubmit();

      expect(emitSpy).toHaveBeenCalledWith({
        email: 'user@example.com',
        password: 'zaq1@WSX',
        loginAsTestUser: false,
      });
    });

    it('should not emit and should mark all fields as touched when the email is invalid', () => {
      const emitSpy = vi.fn();
      component.submitForm.subscribe(emitSpy);

      component.form.setValue({ email: 'not-an-email', password: 'zaq1@WSX' });
      component.onSubmit();

      expect(emitSpy).not.toHaveBeenCalled();
      expect(component.form.get('email')?.touched).toBe(true);
    });

    it('should not emit when the password fails the pattern validator', () => {
      const emitSpy = vi.fn();
      component.submitForm.subscribe(emitSpy);

      component.form.setValue({ email: 'user@example.com', password: 'short' });
      component.onSubmit();

      expect(emitSpy).not.toHaveBeenCalled();
      expect(component.form.invalid).toBe(true);
    });
  });

  describe('password visibility toggle', () => {
    it('should be hidden by default', () => {
      expect(component.showPassword()).toBe(false);
      expect(component.passwordFieldType()).toBe('password');
    });

    it('should switch to text after calling togglePasswordVisibility', () => {
      component.togglePasswordVisibility();

      expect(component.showPassword()).toBe(true);
      expect(component.passwordFieldType()).toBe('text');
    });

    it('should switch back to password when toggled twice', () => {
      component.togglePasswordVisibility();
      component.togglePasswordVisibility();

      expect(component.showPassword()).toBe(false);
      expect(component.passwordFieldType()).toBe('password');
    });

    it('should toggle the rendered input type when the toggle button is clicked', () => {
      const input: HTMLInputElement = fixture.nativeElement.querySelector(
        '[data-testid="login-password-input"]',
      );
      const toggleButton: HTMLButtonElement =
        fixture.nativeElement.querySelector(
          '[data-testid="login-password-toggle-button"]',
        );

      expect(input.type).toBe('password');

      toggleButton.click();
      fixture.detectChanges();

      expect(input.type).toBe('text');
    });
  });

  describe('loginAsTestUser', () => {
    it('should emit submitForm with loginAsTestUser set to true', () => {
      const emitSpy = vi.fn();
      component.submitForm.subscribe(emitSpy);

      component.loginAsTestUser();

      expect(emitSpy).toHaveBeenCalledWith({ loginAsTestUser: true });
    });

    it('should emit when the test-user button is clicked', () => {
      const emitSpy = vi.fn();
      component.submitForm.subscribe(emitSpy);

      const testUserButton: HTMLButtonElement =
        fixture.nativeElement.querySelector(
          '[data-testid="login-test-user-button"]',
        );
      testUserButton.click();

      expect(emitSpy).toHaveBeenCalledWith({ loginAsTestUser: true });
    });
  });
});
