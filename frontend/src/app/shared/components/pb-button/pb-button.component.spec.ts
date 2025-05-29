import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PbButtonComponent } from './pb-button.component';

describe('PbButtonComponent', () => {
  let component: PbButtonComponent;
  let fixture: ComponentFixture<PbButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PbButtonComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PbButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
