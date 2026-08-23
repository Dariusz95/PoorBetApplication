import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CouponService } from '@features/coupons/services/coupon.service';
import { CouponDetails } from '@features/coupons/types/coupon-details';
import { LiveCouponView } from '@features/coupons/types/live-coupon-view';
import { DialogService } from '@shared/services/dialog.service';
import { Uuid } from '@shared/types/uuid.type';
import { getTranslocoModule } from '@shared/utils/get-transloco-module';
import { of } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { LiveMatchCouponsComponent } from './live-match-coupons.component';

describe('LiveMatchCouponsComponent', () => {
  let fixture: ComponentFixture<LiveMatchCouponsComponent>;
  let getCouponDetails: ReturnType<typeof vi.fn>;
  let openCouponDialog: ReturnType<typeof vi.fn>;

  const view: LiveCouponView = {
    couponId: 'coupon-1' as Uuid,
    matchId: 'match-1' as Uuid,
    totalOdds: 3.5,
    potentialPayout: 35,
  };

  beforeEach(async () => {
    getCouponDetails = vi.fn(() => of({} as CouponDetails));
    openCouponDialog = vi.fn();

    await TestBed.configureTestingModule({
      imports: [LiveMatchCouponsComponent, getTranslocoModule()],
      providers: [
        { provide: CouponService, useValue: { getCouponDetails } },
        { provide: DialogService, useValue: { openCouponDialog } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LiveMatchCouponsComponent);
  });

  it('should render nothing when there are no active coupons', () => {
    fixture.detectChanges();

    expect(
      fixture.nativeElement.querySelector('.live-match-coupons'),
    ).toBeNull();
  });

  it('should render a button with the coupon odds', () => {
    fixture.componentRef.setInput('coupons', [view]);
    fixture.detectChanges();

    const button = fixture.nativeElement.querySelector(
      '.live-match-coupons__button',
    );
    expect(button.textContent).toContain('3.50');
  });

  it('should fetch full coupon details and open the dialog on click', () => {
    fixture.componentRef.setInput('coupons', [view]);
    fixture.detectChanges();

    const button: HTMLButtonElement = fixture.nativeElement.querySelector(
      '.live-match-coupons__button',
    );
    button.click();

    expect(getCouponDetails).toHaveBeenCalledWith(view.couponId);
    expect(openCouponDialog).toHaveBeenCalled();
  });
});
