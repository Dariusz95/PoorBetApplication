import { ComponentFixture, TestBed } from '@angular/core/testing';
import { getTranslocoModule } from '@shared/utils/get-transloco-module';
import { beforeEach, describe, expect, it } from 'vitest';

import { CouponSummaryComponent } from './coupon-summary.component';

describe('CouponSummaryComponent', () => {
  let component: CouponSummaryComponent;
  let fixture: ComponentFixture<CouponSummaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CouponSummaryComponent, getTranslocoModule()],
    }).compileComponents();

    fixture = TestBed.createComponent(CouponSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should default to a surfaced summary that shows odds', () => {
    expect(component.surface()).toBe(true);
    expect(component.showOdds()).toBe(true);
  });

  it('should default to the standard translation keys', () => {
    expect(component.amountLabelKey()).toBe('bet.coupon.amount');
    expect(component.oddsLabelKey()).toBe('bet.coupon.totalOdds');
    expect(component.payoutLabelKey()).toBe('bet.coupon.potentialWin');
  });

  it('should allow overriding surface and showOdds via inputs', () => {
    fixture.componentRef.setInput('surface', false);
    fixture.componentRef.setInput('showOdds', false);

    expect(component.surface()).toBe(false);
    expect(component.showOdds()).toBe(false);
  });

  it('should allow overriding label keys via inputs', () => {
    fixture.componentRef.setInput('amountLabelKey', 'custom.amount');

    expect(component.amountLabelKey()).toBe('custom.amount');
  });
});
