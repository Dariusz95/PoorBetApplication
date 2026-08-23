import { AsyncPipe, KeyValuePipe } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  computed,
  inject,
} from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { AuthService } from '@core/auth/services/auth.service';
import { CouponService } from '@features/coupons/services/coupon.service';
import { LiveCouponView } from '@features/coupons/types/live-coupon-view';
import { TranslocoDirective } from '@jsverse/transloco';
import { combineLatest, map, of, switchMap } from 'rxjs';
import { LiveMatchService } from '../../services/live-match.service';
import { LiveMatchEvent } from '../../types/match.types';
import { LiveMatchComponent } from '../live-match-card/live-match.component';
import { LiveMatchSkeletonComponent } from '../live-match-skeleton/live-match-skeleton.component';

@Component({
  selector: 'app-live-matches',
  standalone: true,
  imports: [
    KeyValuePipe,
    LiveMatchComponent,
    LiveMatchSkeletonComponent,
    AsyncPipe,
    TranslocoDirective,
  ],
  templateUrl: './live-matches.component.html',
  styleUrl: './live-matches.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LiveMatchesComponent {
  private readonly liveMatchService = inject(LiveMatchService);
  private readonly couponService = inject(CouponService);
  private readonly authService = inject(AuthService);

  readonly skeletonPlaceholders = [0, 1];

  liveMatches$ = this.liveMatchService.liveMatches$;
  isLoading$ = this.liveMatchService.isLoading$;

  private readonly liveMatches = toSignal(this.liveMatchService.liveMatches$, {
    initialValue: {} as Record<string, LiveMatchEvent>,
  });
  private readonly liveMatchIdsKey = computed(() =>
    Object.keys(this.liveMatches()).sort().join(','),
  );

  protected readonly couponsByMatchId = toSignal(
    combineLatest([
      this.authService.isLoggedIn$,
      toObservable(this.liveMatchIdsKey),
    ]).pipe(
      switchMap(([isLoggedIn, key]) => {
        const matchIds = key ? key.split(',') : [];
        return isLoggedIn && matchIds.length > 0
          ? this.couponService.getMyLiveCoupons(matchIds)
          : of([]);
      }),
      map((views) => {
        const byMatchId = new Map<string, LiveCouponView[]>();
        for (const view of views) {
          const list = byMatchId.get(view.matchId) ?? [];
          list.push(view);
          byMatchId.set(view.matchId, list);
        }
        return byMatchId;
      }),
    ),
    { initialValue: new Map<string, LiveCouponView[]>() },
  );
}
