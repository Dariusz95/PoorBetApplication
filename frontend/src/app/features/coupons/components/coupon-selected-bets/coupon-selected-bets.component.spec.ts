import { ComponentFixture, TestBed } from '@angular/core/testing';
import { getTranslocoModule } from '@shared/utils/get-transloco-module';

import { CouponSelectedBetsComponent } from './coupon-selected-bets.component';
import { beforeEach, describe, expect, it } from 'vitest';

describe('CouponSelectedBetsComponent', () => {
  let component: CouponSelectedBetsComponent;
  let fixture: ComponentFixture<CouponSelectedBetsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CouponSelectedBetsComponent, getTranslocoModule()],
    }).compileComponents();

    fixture = TestBed.createComponent(CouponSelectedBetsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
