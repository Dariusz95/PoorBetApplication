import { ComponentFixture, TestBed } from '@angular/core/testing';
import { signal } from '@angular/core';
import { of } from 'rxjs';
import { AuthService } from '@core/auth/services/auth.service';
import { WalletService } from '@core/wallet/services/wallet.service';
import { UserBalanceComponent } from './user-balance.component';
import { beforeEach, describe, expect, it } from 'vitest';

describe('UserBalanceComponent', () => {
  let component: UserBalanceComponent;
  let fixture: ComponentFixture<UserBalanceComponent>;

  const walletMock = {
    balance: signal<number | null>(125.5),
    getBalance: () => of(125.5),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserBalanceComponent],
      providers: [
        { provide: AuthService, useValue: { isLoggedIn$: of(true) } },
        { provide: WalletService, useValue: walletMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UserBalanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render formatted balance when user is logged in', () => {
    const text = (fixture.nativeElement as HTMLElement).textContent ?? '';
    expect(text).toContain('125.50');
  });
});
