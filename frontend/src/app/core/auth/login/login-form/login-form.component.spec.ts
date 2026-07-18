import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginFormComponent } from './login-form.component';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { getTranslocoModule } from '@shared/utils/get-transloco-module';

describe('LoginFormComponent', () => {
  let component: LoginFormComponent;
  let fixture: ComponentFixture<LoginFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginFormComponent, getTranslocoModule()]
    })
    .compileComponents();

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

      component.form.setValue({ email: 'user@example.com', password: 'zaq1@WSX' });
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
});
