import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AuthService } from '@core/auth/services/auth.service';
import { CouponService } from '@features/coupons/services/coupon.service';
import { LiveCouponView } from '@features/coupons/types/live-coupon-view';
import { BehaviorSubject, of } from 'rxjs';
import { Uuid } from '@shared/types/uuid.type';
import { getTranslocoModule } from '@shared/utils/get-transloco-module';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { LiveMatchService } from '../../services/live-match.service';
import { LiveMatchEvent, MatchEventType } from '../../types/match.types';
import { LiveMatchesComponent } from './live-matches.component';

function event(id: string, minute: number): LiveMatchEvent {
  return {
    id: id as Uuid,
    minute,
    homeTeamId: 'home' as Uuid,
    awayTeamId: 'away' as Uuid,
    homeScore: 0,
    awayScore: 0,
    eventType: MatchEventType.Live,
    eventData: null,
  };
}

describe('LiveMatchesComponent', () => {
  let fixture: ComponentFixture<LiveMatchesComponent>;
  let liveMatches$: BehaviorSubject<Record<string, LiveMatchEvent>>;
  let getMyLiveCoupons: ReturnType<typeof vi.fn>;

  beforeEach(async () => {
    liveMatches$ = new BehaviorSubject<Record<string, LiveMatchEvent>>({
      'match-1': event('match-1', 10),
    });
    getMyLiveCoupons = vi.fn(() => of([] as LiveCouponView[]));

    await TestBed.configureTestingModule({
      imports: [LiveMatchesComponent, getTranslocoModule()],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: LiveMatchService,
          useValue: { liveMatches$, isLoading$: of(false) },
        },
        {
          provide: CouponService,
          useValue: { getMyLiveCoupons },
        },
        {
          provide: AuthService,
          useValue: { isLoggedIn$: of(true) },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LiveMatchesComponent);
    fixture.detectChanges();
  });

  it('should fetch live coupons for the current set of live match ids', () => {
    expect(getMyLiveCoupons).toHaveBeenCalledWith(['match-1']);
  });

  it('should not re-fetch when only a field like minute changes, not the match id set', () => {
    getMyLiveCoupons.mockClear();

    liveMatches$.next({ 'match-1': event('match-1', 11) });
    fixture.detectChanges();

    expect(getMyLiveCoupons).not.toHaveBeenCalled();
  });

  it('should re-fetch when the set of live match ids changes', () => {
    getMyLiveCoupons.mockClear();

    liveMatches$.next({
      'match-1': event('match-1', 11),
      'match-2': event('match-2', 5),
    });
    fixture.detectChanges();

    expect(getMyLiveCoupons).toHaveBeenCalledWith(['match-1', 'match-2']);
  });

  it('should group the flat coupon view list by matchId', () => {
    const viewA: LiveCouponView = {
      couponId: 'coupon-a' as Uuid,
      matchId: 'match-1' as Uuid,
      totalOdds: 2,
      potentialPayout: 20,
    };
    const viewB: LiveCouponView = {
      couponId: 'coupon-b' as Uuid,
      matchId: 'match-1' as Uuid,
      totalOdds: 3,
      potentialPayout: 30,
    };
    // Drop to an empty match-id set first, then bring back match-1 - the key
    // set actually changes each time, so the memoized computed() re-fetches.
    liveMatches$.next({});
    fixture.detectChanges();

    getMyLiveCoupons.mockReturnValue(of([viewA, viewB]));
    liveMatches$.next({ 'match-1': event('match-1', 12) });
    fixture.detectChanges();

    const grouped = (
      fixture.componentInstance as unknown as {
        couponsByMatchId: () => Map<string, LiveCouponView[]>;
      }
    ).couponsByMatchId();
    expect(grouped.get('match-1')).toEqual([viewA, viewB]);
  });
});
