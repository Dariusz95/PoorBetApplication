import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { finalize, Observable, tap } from 'rxjs';
import { environment } from 'src/environments/environment';

interface AccountStateResponse {
  userId: string;
  balance: number;
  level: number;
  currentExp: number;
  requiredExpForNextLevel: number | null;
  winBonusPercent: number;
}

@Injectable({
  providedIn: 'root',
})
export class AccountService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.backend.baseURL}/api/account`;

  private readonly _balance = signal<number | null>(null);
  readonly balance = computed(() => this._balance());

  private readonly _loading = signal(false);
  readonly loading = computed(() => this._loading());

  private readonly _level = signal<number | null>(null);
  readonly level = computed(() => this._level());

  private readonly _currentExp = signal<number | null>(null);
  readonly currentExp = computed(() => this._currentExp());

  private readonly _requiredExpForNextLevel = signal<number | null>(null);
  readonly requiredExpForNextLevel = computed(() =>
    this._requiredExpForNextLevel(),
  );

  private readonly _winBonusPercent = signal<number | null>(null);
  readonly winBonusPercent = computed(() => this._winBonusPercent());

  setBalance(balance: number): void {
    this._balance.set(balance);
  }

  setProgress(
    level: number,
    currentExp: number,
    requiredExpForNextLevel: number | null,
    winBonusPercent: number,
  ): void {
    this._level.set(level);
    this._currentExp.set(currentExp);
    this._requiredExpForNextLevel.set(requiredExpForNextLevel);
    this._winBonusPercent.set(winBonusPercent);
  }

  getAccountState(): Observable<AccountStateResponse> {
    return this.http.get<AccountStateResponse>(`${this.baseUrl}/me`).pipe(
      tap((response) => {
        this._balance.set(response.balance);
        this._level.set(response.level);
        this._currentExp.set(response.currentExp);
        this._requiredExpForNextLevel.set(response.requiredExpForNextLevel);
        this._winBonusPercent.set(response.winBonusPercent);
      }),
    );
  }

  ensureAccountStateLoaded(): void {
    if (this._balance() !== null || this._loading()) {
      return;
    }

    this._loading.set(true);
    this.getAccountState()
      .pipe(finalize(() => this._loading.set(false)))
      .subscribe();
  }
}
