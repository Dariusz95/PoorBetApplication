import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

import { PbFormFieldComponent } from './pb-form-field.component';
import { beforeEach, describe, expect, it } from 'vitest';
import { Subject } from 'rxjs';
import { Component, signal } from '@angular/core';
import {
  FORM_FIELD_CONTROL,
  FormFieldControl,
} from './directives/pb-form-field-control';
import { ErrorValueMap } from '../pb-errors/types/error-value-map';
import { By } from '@angular/platform-browser';
import { getTranslocoModule } from '@shared/utils/get-transloco-module';

@Component({
  selector: 'pb-input',
  standalone: true,
  template: '',
  providers: [
    {
      provide: FORM_FIELD_CONTROL,
      useExisting: FakePbInputComponent,
    },
  ],
})
class FakePbInputComponent implements FormFieldControl {
  stateChanges = new Subject<void>();

  errors = signal<ErrorValueMap | null>(null);
  disabled = signal(false);
  focused = signal(false);
  touched = signal(false);
  invalid = signal(false);
  value = signal('');
  labelId = signal('');
}
@Component({
  standalone: true,
  imports: [PbFormFieldComponent, FakePbInputComponent],
  template: `
    <pb-form-field [showErrors]="showErrors()">
      <pb-input />
    </pb-form-field>
  `,
})
class HostComponent {
  showErrors = signal(true);
}

describe('PbFormFieldComponent', () => {
  let fixture: ComponentFixture<HostComponent>;
  let host: HostComponent;

  let formField: PbFormFieldComponent;
  let fakeInput: FakePbInputComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HostComponent, getTranslocoModule()],
      providers: [provideNoopAnimations()],
    }).compileComponents();

    fixture = TestBed.createComponent(HostComponent);
    host = fixture.componentInstance;

    formField = fixture.debugElement.query(
      By.directive(PbFormFieldComponent),
    ).componentInstance;

    fakeInput = fixture.debugElement.query(
      By.directive(FakePbInputComponent),
    ).componentInstance;

    fixture.detectChanges();
  });

  it('should update focused state', () => {
    fakeInput.focused.set(true);

    fakeInput.stateChanges.next();

    expect(formField.focused()).toBe(true);
  });

  describe('showErrors', () => {
    const makeInvalidAndTouched = () => {
      fakeInput.errors.set({ required: true } as ErrorValueMap);
      fakeInput.touched.set(true);
      fakeInput.invalid.set(true);
      fakeInput.stateChanges.next();
      fixture.detectChanges();
    };

    it('should render pb-errors when touched and invalid by default', () => {
      makeInvalidAndTouched();

      expect(fixture.nativeElement.querySelector('pb-errors')).not.toBeNull();
    });

    it('should not render pb-errors when showErrors is false, even if touched and invalid', () => {
      host.showErrors.set(false);
      fixture.detectChanges();

      makeInvalidAndTouched();

      expect(fixture.nativeElement.querySelector('pb-errors')).toBeNull();
    });
  });
});
