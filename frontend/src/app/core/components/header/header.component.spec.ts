import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { AuthService } from '../../auth/services/auth.service';
import { HeaderComponent } from './header.component';
import { MENU_ITEMS } from './models/menu-items';
import { beforeEach, describe, expect, it } from 'vitest';
import { getTranslocoModule } from '@shared/utils/get-transloco-module';

describe('HeaderComponent', () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HeaderComponent, getTranslocoModule()],
      providers: [{ provide: AuthService, useValue: { isLoggedIn$: of(true) } }],
    }).compileComponents();

    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should expose menu items for header navigation', () => {
    expect(component.menuItems).toEqual(MENU_ITEMS);
  });
});
