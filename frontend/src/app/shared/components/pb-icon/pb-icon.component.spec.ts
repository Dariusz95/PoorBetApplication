import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PbIconComponent } from './pb-icon.component';

describe('PbIconComponent', () => {
  let component: PbIconComponent;
  let fixture: ComponentFixture<PbIconComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PbIconComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PbIconComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
