import { AsyncPipe, DecimalPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { AuthService } from '@core/auth/services/auth.service';
import { WalletService } from '@core/wallet/services/wallet.service';

@Component({
  selector: 'app-user-balance',
  imports: [AsyncPipe, DecimalPipe],
  templateUrl: './user-balance.component.html',
  styleUrl: './user-balance.component.scss',
})
export class UserBalanceComponent {
  private readonly walletService = inject(WalletService);

  protected readonly isLoggedIn$ = inject(AuthService).isLoggedIn$;
  protected readonly balance = this.walletService.balance;

  constructor() {
    this.walletService.getBalance().subscribe();
  }
}
