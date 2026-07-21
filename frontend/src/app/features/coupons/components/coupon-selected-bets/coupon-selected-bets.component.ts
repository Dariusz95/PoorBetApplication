import { animate, style, transition, trigger } from '@angular/animations';
import { DecimalPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { BetSlipService } from '@features/bet/services/bet-slip.service';
import { TranslocoPipe } from '@jsverse/transloco';
import { PbIconComponent } from '@shared/ui/icon/pb-icon.component';
import { PbButtonComponent } from '@shared/ui/pb-button/pb-button.component';
import { EmptyCouponComponent } from '../empty-coupon/coupon-empty.component';
import { BetTypeLabelComponent } from '../bet-type-label/bet-type-label.component';

const reducedMotion =
  typeof window !== 'undefined' &&
  window.matchMedia('(prefers-reduced-motion: reduce)').matches;

export const listItemAnimation = trigger('listItemAnimation', [
  transition(':enter', [
    style({ opacity: 0, transform: 'translateX(-12px) scale(0.97)' }),
    animate(
      `${reducedMotion ? 1 : 240}ms cubic-bezier(0.34, 1.56, 0.64, 1)`,
      style({ opacity: 1, transform: 'translateX(0) scale(1)' }),
    ),
  ]),
  transition(':leave', [
    animate(
      `${reducedMotion ? 1 : 160}ms ease-in`,
      style({ opacity: 0, transform: 'translateX(-12px) scale(0.97)' }),
    ),
  ]),
]);

@Component({
  selector: 'app-coupon-selected-bets',
  imports: [
    EmptyCouponComponent,
    TranslocoPipe,
    DecimalPipe,
    PbButtonComponent,
    PbIconComponent,
    BetTypeLabelComponent,
  ],
  templateUrl: './coupon-selected-bets.component.html',
  styleUrl: './coupon-selected-bets.component.scss',
  animations: [listItemAnimation],
})
export class CouponSelectedBetsComponent {
  protected readonly betSlipService = inject(BetSlipService);

  isStarted(matchStartTime: string): boolean {
    return new Date(matchStartTime).getTime() <= Date.now();
  }
}
