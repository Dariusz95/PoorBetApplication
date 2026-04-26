import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CouponService } from '@features/bet-coupon-card/services/coupon.service';
import { TranslocoDirective } from '@jsverse/transloco';
import { IconComponent } from '@shared/components/icon/icon.component';
import { PbButtonComponent } from '../../shared/components/pb-button/pb-button.component';
import { PbFormFieldComponent } from '../../shared/components/pb-form-field/pb-form-field.component';
import { PbInputComponent } from '../../shared/components/pb-input/pb-input.component';
import { BetSlipService } from '../bet/services/bet-slip.service';
import { CreateCouponRequest } from './models/create-coupon-request';
import { PbLabel } from "@shared/components/pb-form-field/directives/pb-label";

@Component({
  selector: 'app-bet-coupon-card',
  standalone: true,
  imports: [
    DecimalPipe,
    PbButtonComponent,
    FormsModule,
    PbFormFieldComponent,
    ReactiveFormsModule,
    PbInputComponent,
    TranslocoDirective,
    IconComponent,
    PbLabel
],
  templateUrl: './bet-coupon-card.component.html',
  styleUrl: './bet-coupon-card.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BetCouponCardComponent {
  protected readonly betSlipService = inject(BetSlipService);
  protected readonly couponService = inject(CouponService);

  amountCtrl = new FormControl(0);

  submitCoupon() {
    const request = this.getRequest();

    this.couponService.createCoupon(request).subscribe();
  }

  private getRequest(): CreateCouponRequest {
    return {
      stake: this.amountCtrl.value!,
      bets: this.betSlipService.selectedBets().map((bet) => ({
        matchId: bet.matchId,
        betType: bet.betType,
      })),
    };
  }
}
