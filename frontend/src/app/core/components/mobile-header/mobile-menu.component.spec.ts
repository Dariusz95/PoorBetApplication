import { ComponentFixture, TestBed } from '@angular/core/testing';
import { getTranslocoModule } from '@shared/utils/get-transloco-module';
import { beforeEach, describe, expect, it } from 'vitest';
import { MobileMenuComponent } from './mobile-menu.component';
import { MENU_ITEMS } from './models/menu-items';
import { provideRouter } from '@angular/router';

describe('MobileMenuComponent', () => {
  let component: MobileMenuComponent;
  let fixture: ComponentFixture<MobileMenuComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MobileMenuComponent, getTranslocoModule()],
      providers: [provideRouter([])],
    }).compileComponents();
    fixture = TestBed.createComponent(MobileMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should expose configured menu items', () => {
    expect(component.menuItems).toEqual(MENU_ITEMS);
    expect(component.menuItems.length).toBeGreaterThan(0);
  });
});
