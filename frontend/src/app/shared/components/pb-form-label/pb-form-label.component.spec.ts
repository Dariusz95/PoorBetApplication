import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PbFormLabelComponent } from './pb-form-label.component';

describe('PbFormLabelComponent', () => {
  let component: PbFormLabelComponent;
  let fixture: ComponentFixture<PbFormLabelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PbFormLabelComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(PbFormLabelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
