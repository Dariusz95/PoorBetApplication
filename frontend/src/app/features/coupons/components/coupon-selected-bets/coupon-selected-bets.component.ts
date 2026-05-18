import { DecimalPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { BetSlipService } from '@features/bet/services/bet-slip.service';
import { TranslocoPipe } from '@jsverse/transloco';
import { PbIconComponent } from '@shared/ui/icon/pb-icon.component';
import { PbButtonComponent } from '@shared/ui/pb-button/pb-button.component';
import { EmptyCouponComponent } from '../empty-coupon/coupon-empty.component';

@Component({
  selector: 'app-coupon-selected-bets',
  imports: [
    EmptyCouponComponent,
    TranslocoPipe,
    DecimalPipe,
    PbButtonComponent,
    PbIconComponent,
  ],
  templateUrl: './coupon-selected-bets.component.html',
  styleUrl: './coupon-selected-bets.component.scss',
})
export class CouponSelectedBetsComponent {
  protected readonly betSlipService = inject(BetSlipService);
}
