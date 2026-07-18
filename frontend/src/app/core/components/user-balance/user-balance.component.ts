import { AsyncPipe, DecimalPipe } from '@angular/common';
import { Component, inject, input } from '@angular/core';
import { AuthService } from '@core/auth/services/auth.service';
import { WalletService } from '@core/wallet/services/wallet.service';
import { TranslocoPipe } from '@jsverse/transloco';
import { PbImageComponent } from '@shared/ui/pb-image/pb-image.component';
import { PbSpinnerComponent } from '@shared/ui/pb-spinner/pb-spinner.component';
import { filter } from 'rxjs/internal/operators/filter';
import { take } from 'rxjs/internal/operators/take';

@Component({
  selector: 'app-user-balance',
  imports: [
    AsyncPipe,
    DecimalPipe,
    PbSpinnerComponent,
    PbImageComponent,
    TranslocoPipe,
  ],
  templateUrl: './user-balance.component.html',
  styleUrl: './user-balance.component.scss',
})
export class UserBalanceComponent {
  showCoinIcon = input(false);
  private readonly walletService = inject(WalletService);

  protected readonly isLoggedIn$ = inject(AuthService).isLoggedIn$;
  protected readonly balance = this.walletService.balance;
  protected readonly balanceLoading = this.walletService.loading;

  constructor() {
    this.isLoggedIn$.pipe(filter(Boolean), take(1)).subscribe(() => {
      this.walletService.ensureBalanceLoaded();
    });
  }
}
