import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import {
  FormControl,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { CouponService } from '@features/coupons/services/coupon.service';
import { TranslocoPipe } from '@jsverse/transloco';
import { DialogService } from '@shared/services/dialog.service';
import { ToastService } from '@shared/services/toast.service';
import { PbCardBodyDirective } from '@shared/ui/pb-card/directives/pb-card-body.directive';
import { PbCardFooterDirective } from '@shared/ui/pb-card/directives/pb-card-footer.directive.';
import { PbCardHeaderDirective } from '@shared/ui/pb-card/directives/pb-card-header.directive';
import { PbCardComponent } from '@shared/ui/pb-card/pb-card.component';
import { PbLabel } from '@shared/ui/pb-form-field/directives/pb-label';
import { PbButtonComponent } from '../../../../shared/ui/pb-button/pb-button.component';
import { PbFormFieldComponent } from '../../../../shared/ui/pb-form-field/pb-form-field.component';
import { PbInputComponent } from '../../../../shared/ui/pb-input/pb-input.component';
import { BetSlipService } from '../../../bet/services/bet-slip.service';
import { CreateCouponRequest } from '../../models/create-coupon-request';
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

  amountCtrl = new FormControl(0, [Validators.min(1)]);

  submitCoupon(): void {
    const data: any = {
      id: '0796685f-bb78-4cc5-ada3-f9ba43927d2f',
      stake: 2,
      userId: '399a18b3-0d80-42f5-97ad-2878460da8e7',
      reservationId: '49061d9f-8eab-4eff-aec1-e29c75aa1f91',
      status: 'OPEN',
      potentialPayout: 39.7068,
      bets: [
        {
          id: '0c920fec-7b55-4f45-bf85-76c6562f7e83',
          matchId: '04e7d490-bfaf-44a5-837a-199c629f9bc1',
          status: 'PENDING',
          betType: 'AWAY_WIN',
          odds: 4.06,
          version: 0,
        },
        {
          id: '98d2b019-e8af-4f32-be89-ac01d42c500b',
          matchId: '30c58c4d-0f12-4915-b4c1-54b031315499',
          status: 'PENDING',
          betType: 'AWAY_WIN',
          odds: 4.89,
          version: 0,
        },
      ],
    };
    this.dialogService.openCouponDialog(data);
    return;

    // if (!this.amountCtrl.valid) {
    //   this.amountCtrl.markAsTouched();

    //   return;
    // }

    // const request = this.mapToRequest();

    // this.couponService.createCoupon(request).subscribe({
    //   next: (coupon) => {
    //     this.dialogService.openCouponDialog(coupon);
    //     this.resetForm();
    //   },
    //   error: (error) => {
    //     this.toastService.error('Błąd podczas tworzenia kuponu');
    //   },
    // });
  }

  private mapToRequest(): CreateCouponRequest {
    return {
      stake: this.amountCtrl.value!,
      bets: this.betSlipService.selectedBets().map((bet) => ({
        matchId: bet.matchId,
        betType: bet.betType,
      })),
    };
  }

  private resetForm(): void {
    this.amountCtrl.reset(0);
  }
}
