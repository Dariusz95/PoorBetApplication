import { CommonModule } from '@angular/common';
import { Component, inject, Input } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { LiveMatchService } from '@features/bet/services/live-match.service';
import { Bet } from '@features/coupons/models/bet';
import { CouponBetItemComponent } from './coupon-bet-item.component';
import { LiveMatchEvent } from '@features/bet/types/match.types';

@Component({
  selector: 'app-coupon-bets-list',
  standalone: true,
  imports: [CommonModule, CouponBetItemComponent],
  template: `
    <section class="coupon__bets">
      <ul class="coupon__bets-list" role="list">
        @for (bet of bets; track bet.id) {
          <app-coupon-bet-item [bet]="bet" [liveEvent]="liveMatches()[bet.matchId]" />
        }
      </ul>
    </section>
  `,
  styleUrl: './coupon-bets-list.component.scss',
})
export class CouponBetsListComponent {
  private readonly liveMatchService = inject(LiveMatchService);

  @Input() bets: Bet[] = [];

  liveMatches = toSignal(this.liveMatchService.liveMatches$, { initialValue: {} as Record<string, LiveMatchEvent> });
}
