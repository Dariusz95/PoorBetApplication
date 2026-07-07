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
});
