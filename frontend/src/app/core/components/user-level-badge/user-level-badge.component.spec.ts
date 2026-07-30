import { ComponentFixture, TestBed } from '@angular/core/testing';
import { signal } from '@angular/core';
import { of } from 'rxjs';
import { AuthService } from '@core/auth/services/auth.service';
import { AccountService } from '@core/account/services/account.service';
import { UserLevelBadgeComponent } from './user-level-badge.component';
import { beforeEach, describe, expect, it } from 'vitest';
import { getTranslocoModule } from '@shared/utils/get-transloco-module';

describe('UserLevelBadgeComponent', () => {
  let component: UserLevelBadgeComponent;
  let fixture: ComponentFixture<UserLevelBadgeComponent>;

  const accountMock = {
    level: signal<number | null>(5),
    currentExp: signal<number | null>(950),
    requiredExpForNextLevel: signal<number | null>(1500),
    winBonusPercent: signal<number | null>(5),
    loading: signal(false),
    ensureAccountStateLoaded: () => {},
  };

  async function setup(): Promise<void> {
    await TestBed.configureTestingModule({
      imports: [UserLevelBadgeComponent, getTranslocoModule()],
      providers: [
        { provide: AuthService, useValue: { isLoggedIn$: of(true) } },
        { provide: AccountService, useValue: accountMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UserLevelBadgeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  beforeEach(async () => {
    await setup();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the compact level badge by default', () => {
    const text = (fixture.nativeElement as HTMLElement).textContent ?? '';
    expect(text).toContain('5');
  });

  it('should render EXP progress and win bonus when expanded', async () => {
    fixture.componentRef.setInput('expanded', true);
    fixture.detectChanges();

    const el = fixture.nativeElement as HTMLElement;
    const progressbar = el.querySelector('[role="progressbar"]');

    expect(progressbar).toBeTruthy();
    expect(progressbar?.getAttribute('aria-valuenow')).toBe('950');
    expect(progressbar?.getAttribute('aria-valuemax')).toBe('1500');
    expect(el.querySelector('.user-level-badge__bonus')).toBeTruthy();
  });
});
