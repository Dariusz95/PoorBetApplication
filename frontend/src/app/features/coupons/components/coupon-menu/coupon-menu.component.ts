import { Component, inject } from '@angular/core';
import { BetSlipService } from '@features/bet/services/bet-slip.service';
import { TranslocoPipe } from '@jsverse/transloco';
import { PbIconComponent } from '@shared/ui/icon/pb-icon.component';
import { PbPopoverComponent } from '@shared/ui/pb-popover/pb-popover.component';

@Component({
  selector: 'app-coupon-menu',
  imports: [PbPopoverComponent, PbIconComponent, TranslocoPipe],
  templateUrl: './coupon-menu.component.html',
  styleUrl: './coupon-menu.component.scss',
})
export class CouponMenuComponent {
  protected readonly betSlipService = inject(BetSlipService);
}
