import { AsyncPipe } from '@angular/common';
import { Component, computed, inject, input } from '@angular/core';
import { AuthService } from '@core/auth/services/auth.service';
import { AccountService } from '@core/account/services/account.service';
import { TranslocoPipe } from '@jsverse/transloco';
import { filter } from 'rxjs/internal/operators/filter';
import { take } from 'rxjs/internal/operators/take';

@Component({
  selector: 'app-user-level-badge',
  imports: [AsyncPipe, TranslocoPipe],
  templateUrl: './user-level-badge.component.html',
  styleUrl: './user-level-badge.component.scss',
})
export class UserLevelBadgeComponent {
  expanded = input(false);
  private readonly accountService = inject(AccountService);

  protected readonly isLoggedIn$ = inject(AuthService).isLoggedIn$;
  protected readonly level = this.accountService.level;
  protected readonly currentExp = this.accountService.currentExp;
  protected readonly requiredExpForNextLevel =
    this.accountService.requiredExpForNextLevel;
  protected readonly winBonusPercent = this.accountService.winBonusPercent;
  protected readonly progressLoading = this.accountService.loading;

  protected readonly progressPercent = computed(() => {
    const current = this.currentExp();
    const required = this.requiredExpForNextLevel();

    if (current === null || required === null || required <= 0) {
      return 100;
    }

    return Math.min(100, Math.round((current / required) * 100));
  });

  constructor() {
    this.isLoggedIn$.pipe(filter(Boolean), take(1)).subscribe(() => {
      this.accountService.ensureAccountStateLoaded();
    });
  }
}
