import { inject, Injectable } from '@angular/core';
import { BetSlipService } from '@features/bet/services/bet-slip.service';
import { DialogService } from '@shared/services/dialog.service';
import { ToastService } from '@shared/services/toast.service';
import { CouponService } from './coupon.service';
import { CreateCouponRequest } from '../models/create-coupon-request';

@Injectable({
  providedIn: 'root',
})
export class BetCouponFacadeService {
  private readonly betSlipService = inject(BetSlipService);
  private readonly couponService = inject(CouponService);
  private readonly dialogService = inject(DialogService);
  private readonly toastService = inject(ToastService);

  createCoupon(amount: number): void {
    const request = this.mapToRequest(amount);

    this.couponService.createCoupon(request).subscribe({
      next: (coupon) => {
        this.dialogService.openCouponDialog(coupon);
        // this.resetForm();
      },
      error: (error) => {
        this.toastService.error('Błąd podczas tworzenia kuponu');
      },
    });
  }
  private mapToRequest(amount: number): CreateCouponRequest {
    return {
      stake: amount,
      bets: this.betSlipService.selectedBets().map((bet) => ({
        matchId: bet.matchId,
        betType: bet.betType,
      })),
    };
  }
}
