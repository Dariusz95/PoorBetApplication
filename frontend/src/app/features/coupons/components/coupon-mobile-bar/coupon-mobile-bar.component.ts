import { DecimalPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { TranslocoPipe } from '@jsverse/transloco';
import { DialogService } from '@shared/services/dialog.service';
import { PbIconComponent } from '@shared/ui/icon/pb-icon.component';
import { BetSlipStore } from '../../../bet/services/bet-slip.store';

@Component({
  selector: 'app-coupon-mobile-bar',
  imports: [DecimalPipe, TranslocoPipe, PbIconComponent],
  templateUrl: './coupon-mobile-bar.component.html',
  styleUrl: './coupon-mobile-bar.component.scss',
})
export class CouponMobileBarComponent {
  protected readonly betSlipStore = inject(BetSlipStore);
  private readonly dialogService = inject(DialogService);

  openSheet(): void {
    this.dialogService.openCouponSheet();
  }
}
