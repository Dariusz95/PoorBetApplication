import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CouponDropdownComponent } from './coupon-dropdown.component';
import { beforeEach, describe, expect, it } from 'vitest';
import { getTranslocoModule } from '@shared/utils/get-transloco-module';

describe('CouponDropdownComponent', () => {
  let component: CouponDropdownComponent;
  let fixture: ComponentFixture<CouponDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CouponDropdownComponent, getTranslocoModule()],
    }).compileComponents();

    fixture = TestBed.createComponent(CouponDropdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the coupon dropdown trigger icon', () => {
    const trigger = fixture.nativeElement.querySelector(
      '.pb-popover__trigger',
    );

    expect(trigger).toBeTruthy();
  });
});
