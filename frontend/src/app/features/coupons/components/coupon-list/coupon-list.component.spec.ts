import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RoutePath } from '@core/routing/route-path';
import { RoutingService } from '@core/routing/routing.service';
import { getTranslocoModule } from '@shared/utils/get-transloco-module';
import { DialogService } from '@shared/services/dialog.service';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { of } from 'rxjs';
import { PageResponse } from '@shared/interfaces/page-response';
import { CouponListComponent } from './coupon-list.component';
import { CouponService } from '../../services/coupon.service';
import { CouponStatus } from '../../types/coupon-status';
import { Coupon } from '../../types/coupon';
import { CouponDetails } from '../../types/coupon-details';

function emptyPage(): PageResponse<Coupon> {
  return {
    content: [],
    totalElements: 0,
    totalPages: 0,
    size: 20,
    number: 0,
    first: true,
    last: true,
  };
}

describe('CouponListComponent', () => {
  let component: CouponListComponent;
  let fixture: ComponentFixture<CouponListComponent>;
  let couponService: {
    getMyCoupons: ReturnType<typeof vi.fn>;
    getCouponDetails: ReturnType<typeof vi.fn>;
  };
  let routingService: { navigateTo: ReturnType<typeof vi.fn> };
  let dialogService: { openCouponDialog: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    couponService = {
      getMyCoupons: vi.fn().mockReturnValue(of(emptyPage())),
      getCouponDetails: vi.fn(),
    };
    routingService = { navigateTo: vi.fn() };
    dialogService = { openCouponDialog: vi.fn() };

    await TestBed.configureTestingModule({
      imports: [CouponListComponent, getTranslocoModule()],
      providers: [
        { provide: CouponService, useValue: couponService },
        { provide: RoutingService, useValue: routingService },
        { provide: DialogService, useValue: dialogService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(CouponListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should default to the open tab', () => {
    expect(component.activeTab()).toBe('open');
  });

  it('should fetch open coupons on init', () => {
    expect(couponService.getMyCoupons).toHaveBeenCalledWith(
      expect.objectContaining({ page: 0, size: 20 }),
      { statuses: [CouponStatus.Open] },
    );
  });

  describe('selectTab', () => {
    it('should update the active tab', () => {
      component.selectTab('won');

      expect(component.activeTab()).toBe('won');
    });

    it('should refetch coupons filtered for the won tab', () => {
      component.selectTab('won');
      fixture.detectChanges();

      expect(couponService.getMyCoupons).toHaveBeenCalledWith(
        expect.anything(),
        { statuses: [CouponStatus.Won] },
      );
    });

    it('should refetch coupons filtered for won and lost on the settled tab', () => {
      component.selectTab('settled');
      fixture.detectChanges();

      expect(couponService.getMyCoupons).toHaveBeenCalledWith(
        expect.anything(),
        { statuses: [CouponStatus.Lost, CouponStatus.Won] },
      );
    });
  });

  describe('coupons / total', () => {
    it('should expose the coupons and total from the current page', () => {
      const coupon: Coupon = {
        id: 'c1',
        stake: 10,
        status: CouponStatus.Open,
        potentialPayout: 20,
        createdAt: new Date().toISOString(),
      };
      couponService.getMyCoupons.mockReturnValue(
        of({ ...emptyPage(), content: [coupon], totalElements: 1 }),
      );

      component.selectTab('won');
      fixture.detectChanges();

      expect(component.coupons()).toEqual([coupon]);
      expect(component.total()).toBe(1);
    });
  });

  describe('navigateToMyCoupons', () => {
    it('should emit seeAll and navigate to the my-coupons route', () => {
      const seeAllSpy = vi.fn();
      component.seeAll.subscribe(seeAllSpy);

      component.navigateToMyCoupons();

      expect(seeAllSpy).toHaveBeenCalled();
      expect(routingService.navigateTo).toHaveBeenCalledWith(
        RoutePath.MyCoupons,
      );
    });
  });

  describe('openDetails', () => {
    it('should fetch coupon details and open the dialog', () => {
      const details = { id: 'c1' } as CouponDetails;
      couponService.getCouponDetails.mockReturnValue(of(details));

      component.openDetails({
        id: 'c1',
        stake: 10,
        status: CouponStatus.Open,
        potentialPayout: 20,
        createdAt: new Date().toISOString(),
      });

      expect(couponService.getCouponDetails).toHaveBeenCalledWith('c1');
      expect(dialogService.openCouponDialog).toHaveBeenCalledWith(details);
    });
  });

  describe('emptyTitleKey', () => {
    it('should return the empty-won key on the won tab', () => {
      component.selectTab('won');

      expect(component.emptyTitleKey()).toBe('couponList.emptyWonTitle');
    });

    it('should return the empty-settled key on the settled tab', () => {
      component.selectTab('settled');

      expect(component.emptyTitleKey()).toBe('couponList.emptySettledTitle');
    });

    it('should return the default empty key on the open tab', () => {
      expect(component.emptyTitleKey()).toBe('couponList.emptyTitle');
    });
  });
});
