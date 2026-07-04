import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import {
  FormControl,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { CouponService } from '@features/coupons/services/coupon.service';
import { TranslocoPipe, TranslocoService } from '@jsverse/transloco';
import { DialogService } from '@shared/services/dialog.service';
import { ToastService } from '@shared/services/toast.service';
import { PbIconComponent } from '@shared/ui/icon/pb-icon.component';
import { PbCardBodyDirective } from '@shared/ui/pb-card/directives/pb-card-body.directive';
import { PbCardFooterDirective } from '@shared/ui/pb-card/directives/pb-card-footer.directive.';
import { PbCardHeaderDirective } from '@shared/ui/pb-card/directives/pb-card-header.directive';
import { PbCardComponent } from '@shared/ui/pb-card/pb-card.component';
import { PbLabel } from '@shared/ui/pb-form-field/directives/pb-label';
import { PbButtonComponent } from '../../../../shared/ui/pb-button/pb-button.component';
import { PbFormFieldComponent } from '../../../../shared/ui/pb-form-field/pb-form-field.component';
import { PbInputComponent } from '../../../../shared/ui/pb-input/pb-input.component';
import { BetSlipService } from '../../../bet/services/bet-slip.service';
import { CreateCouponRequest } from '../../types/create-coupon-request';
import { CouponSelectedBetsComponent } from '../coupon-selected-bets/coupon-selected-bets.component';
import { CouponSummaryComponent } from '../coupon-summary/coupon-summary.component';

@Component({
  selector: 'app-coupon-card',
  standalone: true,
  imports: [
    PbButtonComponent,
    FormsModule,
    PbFormFieldComponent,
    ReactiveFormsModule,
    PbInputComponent,
    PbLabel,
    PbCardComponent,
    PbCardBodyDirective,
    PbCardHeaderDirective,
    PbCardFooterDirective,
    TranslocoPipe,
    CouponSelectedBetsComponent,
    CouponSummaryComponent,
    DecimalPipe,
    PbIconComponent,
  ],
  templateUrl: './coupon-card.component.html',
  styleUrl: './coupon-card.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CouponCardComponent {
  protected readonly betSlipService = inject(BetSlipService);
  protected readonly couponService = inject(CouponService);
  protected readonly dialogService = inject(DialogService);
  protected readonly toastService = inject(ToastService);
  private readonly transloco = inject(TranslocoService);

  couponStakeCtrl = new FormControl(null, [Validators.min(1)]);

  submitCoupon(): void {
    if (!this.couponStakeCtrl.valid) {
      this.couponStakeCtrl.markAsTouched();

      return;
    }

    const startedBet = this.betSlipService
      .selectedBets()
      .find((bet) => new Date(bet.matchStartTime).getTime() <= Date.now());
    if (startedBet) {
      this.toastService.error(
        this.transloco.translate('coupon.matchStartedToast'),
      );
      return;
    }

    const request = this.mapToRequest();

    this.couponService.create(request).subscribe({
      next: (coupon) => {
        this.dialogService.openCouponDialog(coupon);
      },
      error: () => {
        this.toastService.error(this.transloco.translate('bet.coupon.error'));
      },
    });
  }

  private mapToRequest(): CreateCouponRequest {
    return {
      stake: this.couponStakeCtrl.value!,
      bets: this.betSlipService.selectedBets().map((bet) => ({
        matchId: bet.matchId,
        betType: bet.betType,
      })),
    };
  }
}
