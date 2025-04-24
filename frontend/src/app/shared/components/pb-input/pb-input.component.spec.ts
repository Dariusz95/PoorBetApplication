import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PbInputComponent } from './pb-input.component';

describe('PbInputComponent', () => {
  let component: PbInputComponent;
  let fixture: ComponentFixture<PbInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PbInputComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PbInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
