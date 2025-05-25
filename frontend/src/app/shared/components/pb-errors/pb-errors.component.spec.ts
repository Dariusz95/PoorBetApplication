import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PbErrorsComponent } from './pb-errors.component';

describe('PbErrorsComponent', () => {
  let component: PbErrorsComponent;
  let fixture: ComponentFixture<PbErrorsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PbErrorsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PbErrorsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
