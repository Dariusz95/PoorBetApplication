import { ComponentFixture, TestBed } from '@angular/core/testing';
import { getTranslocoModule } from '@shared/utils/get-transloco-module';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { CouponListItemComponent } from './coupon-list-item.component';
import { Coupon } from '../../../../types/coupon';
import { CouponStatus } from '../../../../types/coupon-status';

function coupon(status: CouponStatus): Coupon {
  return {
    id: 'c1',
    stake: 10,
    status,
    potentialPayout: 20,
    createdAt: new Date().toISOString(),
  };
}

describe('CouponListItemComponent', () => {
  let component: CouponListItemComponent;
  let fixture: ComponentFixture<CouponListItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CouponListItemComponent, getTranslocoModule()],
    }).compileComponents();

    fixture = TestBed.createComponent(CouponListItemComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('coupon', coupon(CouponStatus.Open));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('statusLabelKey', () => {
    it('should return the won key', () => {
      fixture.componentRef.setInput('coupon', coupon(CouponStatus.Won));

      expect(component.statusLabelKey()).toBe('couponList.statusWon');
    });

    it('should return the lost key', () => {
      fixture.componentRef.setInput('coupon', coupon(CouponStatus.Lost));

      expect(component.statusLabelKey()).toBe('couponList.statusLost');
    });

    it('should return the open key', () => {
      fixture.componentRef.setInput('coupon', coupon(CouponStatus.Open));

      expect(component.statusLabelKey()).toBe('couponList.statusOpen');
    });
  });

  describe('open', () => {
    it('should emit when the card is clicked', () => {
      const openSpy = vi.fn();
      component.open.subscribe(openSpy);

      fixture.nativeElement
        .querySelector('.coupon-card')
        .dispatchEvent(new MouseEvent('click'));

      expect(openSpy).toHaveBeenCalled();
    });
  });
});
