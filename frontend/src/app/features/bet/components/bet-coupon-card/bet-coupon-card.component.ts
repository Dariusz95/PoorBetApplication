import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TranslocoDirective } from '@jsverse/transloco';
import { PbButtonComponent } from '../../../../shared/components/pb-button/pb-button.component';
import { PbFormFieldComponent } from '../../../../shared/components/pb-form-field/pb-form-field.component';
import { PbInputComponent } from '../../../../shared/components/pb-input/pb-input.component';
import { BetSlipService } from '../../services/bet-slip.service';

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
  ],
  templateUrl: './bet-coupon-card.component.html',
  styleUrl: './bet-coupon-card.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BetCouponCardComponent {
  protected readonly betSlipService = inject(BetSlipService);
  amount: number = 0;

  amountCtrl = new FormControl(0);
}
