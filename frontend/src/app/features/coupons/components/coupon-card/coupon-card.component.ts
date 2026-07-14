import { DialogRef } from '@angular/cdk/dialog';
import { DecimalPipe } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  inject,
  signal,
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import {
  FormControl,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { AuthService } from '@core/auth/services/auth.service';
import { CouponService } from '@features/coupons/services/coupon.service';
import { TranslocoPipe, TranslocoService } from '@jsverse/transloco';
import { DialogService } from '@shared/services/dialog.service';
import { ToastService } from '@shared/services/toast.service';
import { PbIconComponent } from '@shared/ui/icon/pb-icon.component';
import { PbCardBodyDirective } from '@shared/ui/pb-card/directives/pb-card-body.directive';
import { PbCardFooterDirective } from '@shared/ui/pb-card/directives/pb-card-footer.directive';
import { PbCardHeaderDirective } from '@shared/ui/pb-card/directives/pb-card-header.directive';
import { PbCardComponent } from '@shared/ui/pb-card/pb-card.component';
import { PbLabel } from '@shared/ui/pb-form-field/directives/pb-label';
import { filter, finalize } from 'rxjs';
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
  private readonly authService = inject(AuthService);
  private readonly transloco = inject(TranslocoService);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly dialogRef = inject(DialogRef, { optional: true });

  protected readonly isLoggedIn = toSignal(this.authService.isLoggedIn$, {
    initialValue: false,
  });

  couponStakeCtrl = new FormControl(null, [
    Validators.required,
    Validators.min(1),
  ]);
  readonly submitting = signal(false);

  constructor() {
    if (this.dialogRef) {
      this.dialogRef.backdropClick
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe(() => this.close());

      this.dialogRef.keydownEvents
        .pipe(
          filter((event) => event.key === 'Escape'),
          takeUntilDestroyed(this.destroyRef),
        )
        .subscribe(() => this.close());
    }
  }

  close(): void {
    this.dialogRef?.close();
  }

  submitCoupon(): void {
    if (!this.isLoggedIn()) {
      return;
    }

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

    this.submitting.set(true);
    this.couponService
      .create(request)
      .pipe(finalize(() => this.submitting.set(false)))
      .subscribe({
        next: (coupon) => {
          this.dialogService.openCouponDialog(coupon);
          this.close();
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
