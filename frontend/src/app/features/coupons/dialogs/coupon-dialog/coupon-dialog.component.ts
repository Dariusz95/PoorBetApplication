import {
  animate,
  AnimationEvent,
  style,
  transition,
  trigger,
} from '@angular/animations';
import { DIALOG_DATA, DialogRef } from '@angular/cdk/dialog';
import { CommonModule } from '@angular/common';
import {
  Component,
  computed,
  DestroyRef,
  effect,
  inject,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { filter } from 'rxjs';
import { BetStatus } from '@features/coupons/types/bet-status';
import { CouponStatus } from '@features/coupons/types/coupon-status';
import { CouponService } from '@features/coupons/services/coupon.service';
import { CouponSummaryComponent } from '@features/coupons/components/coupon-summary/coupon-summary.component';
import { CouponDetails } from '@features/coupons/types/coupon-details';
import { LiveMatchService } from '@features/bet/services/live-match.service';
import { MatchEventType } from '@features/bet/types/match.types';
import { TranslocoPipe } from '@jsverse/transloco';
import { PbIconComponent } from '@shared/ui/icon/pb-icon.component';
import { PbButtonComponent } from '../../../../shared/ui/pb-button/pb-button.component';
import { PbCardBodyDirective } from '../../../../shared/ui/pb-card/directives/pb-card-body.directive';
import { PbCardHeaderDirective } from '../../../../shared/ui/pb-card/directives/pb-card-header.directive';
import { PbCardComponent } from '../../../../shared/ui/pb-card/pb-card.component';
import { CouponBetsListComponent } from './components/coupon-bets-list.component';

const dialogAnimation = trigger('dialogAnimation', [
  transition(
    ':enter',
    [
      style({ opacity: 0, transform: 'scale(0.92) translateY(12px)' }),
      animate(
        '{{enterDuration}}ms cubic-bezier(0.34, 1.56, 0.64, 1)',
        style({ opacity: 1, transform: 'scale(1) translateY(0)' }),
      ),
    ],
    { params: { enterDuration: 320 } },
  ),
  transition(
    '* => closing',
    [
      animate(
        '{{leaveDuration}}ms ease-in',
        style({ opacity: 0, transform: 'scale(0.96) translateY(6px)' }),
      ),
    ],
    { params: { leaveDuration: 160 } },
  ),
]);

@Component({
  selector: 'coupon-dialog',
  standalone: true,
  imports: [
    CommonModule,
    TranslocoPipe,
    PbCardComponent,
    CouponBetsListComponent,
    PbCardHeaderDirective,
    PbCardBodyDirective,
    PbButtonComponent,
    PbIconComponent,
    CouponSummaryComponent,
  ],
  templateUrl: './coupon-dialog.component.html',
  styleUrl: './coupon-dialog.component.scss',
  animations: [dialogAnimation],
  host: {
    '[@dialogAnimation]': 'animationBinding()',
    '(@dialogAnimation.done)': 'onAnimationDone($event)',
  },
})
export class PbCouponDialogComponent {
  private readonly dialogRef = inject(DialogRef<void>);
  private readonly couponService = inject(CouponService);
  private readonly liveMatchService = inject(LiveMatchService);
  private readonly destroyRef = inject(DestroyRef);

  private readonly reducedMotion =
    typeof window !== 'undefined' &&
    window.matchMedia('(prefers-reduced-motion: reduce)').matches;

  readonly animationState = signal<'open' | 'closing'>('open');

  readonly animationBinding = computed(() => ({
    value: this.animationState(),
    params: {
      enterDuration: this.reducedMotion ? 1 : 320,
      leaveDuration: this.reducedMotion ? 1 : 160,
    },
  }));

  readonly coupon = signal<CouponDetails>(inject(DIALOG_DATA));

  readonly wonCount = computed(
    () => this.coupon().bets.filter((b) => b.status === BetStatus.Won).length,
  );
  readonly lostCount = computed(
    () => this.coupon().bets.filter((b) => b.status === BetStatus.Lost).length,
  );

  readonly CouponStatus = CouponStatus;

  constructor() {
    this.dialogRef.backdropClick
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.closeCoupon());

    this.dialogRef.keydownEvents
      .pipe(
        filter((event) => event.key === 'Escape'),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe(() => this.closeCoupon());

    this.liveMatchService.liveMatches$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((liveMatches) => {
        const couponMatchIds = new Set(
          this.coupon().bets.map((b) => b.matchId),
        );
        const hasEndedMatch = Object.values(liveMatches).some(
          (event) =>
            couponMatchIds.has(event.id) &&
            event.eventType === MatchEventType.MatchEnded,
        );

        if (hasEndedMatch) {
          this.couponService
            .getCouponDetails(this.coupon().id)
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe((fresh) => this.coupon.set(fresh));
        }
      });
  }

  closeCoupon(): void {
    if (this.animationState() === 'closing') return;

    this.dialogRef.overlayRef.backdropElement?.classList.add('is-closing');
    this.animationState.set('closing');
  }

  onAnimationDone(event: AnimationEvent): void {
    if (event.toState === 'closing') {
      this.dialogRef.close();
    }
  }
}
