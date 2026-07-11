import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { AuthService } from '@core/auth/services/auth.service';
import { getTranslocoModule } from '@shared/utils/get-transloco-module';
import { of } from 'rxjs';
import { beforeEach, describe, expect, it } from 'vitest';
import { MobileMenuComponent } from './mobile-menu.component';
import { MENU_ITEMS } from './models/menu-items';

describe('MobileMenuComponent', () => {
  let component: MobileMenuComponent;
  let fixture: ComponentFixture<MobileMenuComponent>;

  async function setup(isLoggedIn: boolean): Promise<void> {
    await TestBed.configureTestingModule({
      imports: [MobileMenuComponent, getTranslocoModule()],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: { isLoggedIn$: of(isLoggedIn) } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MobileMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  it('should create', async () => {
    await setup(false);

    expect(component).toBeTruthy();
  });

  it('should hide items that require auth for guests', async () => {
    await setup(false);

    expect(component.menuItems()).toEqual(
      MENU_ITEMS.filter((item) => !item.requiresAuth),
    );
  });

  it('should expose all menu items when logged in', async () => {
    await setup(true);

    expect(component.menuItems()).toEqual(MENU_ITEMS);
  });
});
