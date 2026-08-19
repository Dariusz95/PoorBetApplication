import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { RegisterFormComponent } from './register-form.component';
import { getTranslocoModule } from '@shared/utils/get-transloco-module';

describe('RegisterFormComponent', () => {
  let component: RegisterFormComponent;
  let fixture: ComponentFixture<RegisterFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegisterFormComponent, getTranslocoModule()],
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterFormComponent);
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
        confirmPassword: 'zaq1@WSX',
      });
      component.onSubmit();

      expect(emitSpy).toHaveBeenCalledWith({
        email: 'user@example.com',
        password: 'zaq1@WSX',
        confirmPassword: 'zaq1@WSX',
      });
    });

    it('should not emit and should mark all fields as touched when passwords do not match', () => {
      const emitSpy = vi.fn();
      component.submitForm.subscribe(emitSpy);

      component.form.setValue({
        email: 'user@example.com',
        password: 'zaq1@WSX',
        confirmPassword: 'differentPass1@',
      });
      component.onSubmit();

      expect(emitSpy).not.toHaveBeenCalled();
      expect(component.form.get('confirmPassword')?.touched).toBe(true);
      expect(component.form.errors).toEqual({ passwordMismatch: true });
    });

    it('should not emit when the email is invalid', () => {
      const emitSpy = vi.fn();
      component.submitForm.subscribe(emitSpy);

      component.form.setValue({
        email: 'not-an-email',
        password: 'zaq1@WSX',
        confirmPassword: 'zaq1@WSX',
      });
      component.onSubmit();

      expect(emitSpy).not.toHaveBeenCalled();
    });
  });

  describe('password visibility toggles', () => {
    it('should be hidden by default for both password fields', () => {
      expect(component.passwordFieldType()).toBe('password');
      expect(component.confirmPasswordFieldType()).toBe('password');
    });

    it('should reveal only the password field when its toggle is used', () => {
      component.togglePasswordVisibility();

      expect(component.passwordFieldType()).toBe('text');
      expect(component.confirmPasswordFieldType()).toBe('password');
    });

    it('should reveal only the confirm password field when its toggle is used', () => {
      component.toggleConfirmPasswordVisibility();

      expect(component.confirmPasswordFieldType()).toBe('text');
      expect(component.passwordFieldType()).toBe('password');
    });
  });

  describe('passwordRequirements', () => {
    const metRequirement = (labelKey: string) =>
      component
        .passwordRequirements()
        .find((requirement) => requirement.labelKey === labelKey)?.met;

    it('should report the content requirements as unmet for an empty password', () => {
      component.form.controls.password.setValue('');

      const requirements = component.passwordRequirements();
      const contentRequirements = requirements.filter(
        (r) => r.labelKey !== 'auth.register.passwordRequirements.allowedChars',
      );

      expect(contentRequirements.every((r) => !r.met)).toBe(true);
      expect(component.passwordValid()).toBe(false);
    });

    it('should report all requirements as met for a fully valid password', () => {
      component.form.controls.password.setValue('Abcdef1!');

      expect(component.passwordRequirements().every((r) => r.met)).toBe(true);
      expect(component.passwordValid()).toBe(true);
    });

    it('should flag a disallowed character even if a valid special character is also present', () => {
      component.form.controls.password.setValue('aaaaaaaaaaaaaaaA4.?');

      expect(metRequirement('auth.register.passwordRequirements.length')).toBe(
        true,
      );
      expect(
        metRequirement('auth.register.passwordRequirements.lowercase'),
      ).toBe(true);
      expect(
        metRequirement('auth.register.passwordRequirements.uppercase'),
      ).toBe(true);
      expect(metRequirement('auth.register.passwordRequirements.digit')).toBe(
        true,
      );
      expect(metRequirement('auth.register.passwordRequirements.special')).toBe(
        true,
      );
      expect(
        metRequirement('auth.register.passwordRequirements.allowedChars'),
      ).toBe(false);
      expect(component.passwordValid()).toBe(false);
    });
  });

  describe('passwordTouched', () => {
    it('should be false before the password field has been touched', () => {
      expect(component.passwordTouched()).toBe(false);
    });

    it('should become true once the password field is marked as touched', () => {
      component.form.controls.password.markAsTouched();

      expect(component.passwordTouched()).toBe(true);
    });
  });
});
