import { ComponentFixture, TestBed } from '@angular/core/testing';
import { getTranslocoModule } from '@shared/utils/get-transloco-module';

import { beforeEach, describe, expect, it } from 'vitest';
import { ErrorValueMap } from './types/error-value-map';
import { PbErrorsComponent } from './pb-errors.component';

describe('PbErrorsComponent', () => {
  let component: PbErrorsComponent;
  let fixture: ComponentFixture<PbErrorsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PbErrorsComponent, getTranslocoModule()],
    }).compileComponents();

    fixture = TestBed.createComponent(PbErrorsComponent);

    component = fixture.componentInstance;
    fixture.componentRef.setInput('errors', null);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return no form errors when there are none', () => {
    expect(component.formErrors()).toEqual([]);
  });

  it('should map a required error without params', () => {
    fixture.componentRef.setInput('errors', { required: true } as ErrorValueMap);

    expect(component.formErrors()).toEqual([
      { translateKey: 'validation.required', params: {} },
    ]);
  });

  it('should map a minlength error with the required length as a param', () => {
    fixture.componentRef.setInput('errors', {
      minlength: { requiredLength: 8, actualLength: 3 },
    } as ErrorValueMap);

    expect(component.formErrors()).toEqual([
      { translateKey: 'validation.minlength', params: { minLength: 8 } },
    ]);
  });

  it('should map a maxlength error with the required length as a param', () => {
    fixture.componentRef.setInput('errors', {
      maxlength: { requiredLength: 20, actualLength: 30 },
    } as ErrorValueMap);

    expect(component.formErrors()).toEqual([
      { translateKey: 'validation.maxlength', params: { maxLength: 20 } },
    ]);
  });

  it('should only return the first error by default (displaySingleError=true)', () => {
    fixture.componentRef.setInput('errors', {
      required: true,
      email: true,
    } as ErrorValueMap);

    expect(component.formErrors().length).toBe(1);
    expect(component.formErrors()[0].translateKey).toBe('validation.required');
  });

  it('should return every error when displaySingleError is false', () => {
    fixture.componentRef.setInput('displaySingleError', false);
    fixture.componentRef.setInput('errors', {
      required: true,
      email: true,
    } as ErrorValueMap);

    expect(component.formErrors().length).toBe(2);
  });
});
