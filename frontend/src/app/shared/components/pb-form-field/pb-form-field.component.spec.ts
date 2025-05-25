import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PbFormFieldComponent } from './pb-form-field.component';

describe('PbInputComponent', () => {
  let component: PbFormFieldComponent;
  let fixture: ComponentFixture<PbFormFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PbFormFieldComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(PbFormFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
