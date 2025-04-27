import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PbDropdownComponent } from './pb-dropdown.component';

describe('PbDropdownComponent', () => {
  let component: PbDropdownComponent;
  let fixture: ComponentFixture<PbDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PbDropdownComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PbDropdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
