import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CouponSelectedBetsComponent } from './coupon-selected-bets.component';
import { beforeEach, describe, expect, it } from 'vitest';

describe('CouponSelectedBetsComponent', () => {
  let component: CouponSelectedBetsComponent;
  let fixture: ComponentFixture<CouponSelectedBetsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CouponSelectedBetsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CouponSelectedBetsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
