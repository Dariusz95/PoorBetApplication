import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PbCardComponent } from './pb-card.component';

describe('PbCardComponent', () => {
  let component: PbCardComponent;
  let fixture: ComponentFixture<PbCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PbCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PbCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
