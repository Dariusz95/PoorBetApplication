import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TranslocoDirective } from '@jsverse/transloco';
import { LanguageSwitcherComponent } from '../../../shared/components/language-switcher/language-switcher.component';
import { PbButtonComponent } from '../../../shared/components/pb-button/pb-button.component';
import { ThemeToggleComponent } from '../../../shared/components/theme-toggle/theme-toggle.component';
import { RoutePath } from '../../routing/route-path';
import { MENU_ITEMS } from './models/menu-items';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    PbButtonComponent,
    ThemeToggleComponent,
    LanguageSwitcherComponent,
    TranslocoDirective,
    PbButtonComponent,
  ],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent {
  protected readonly RoutePath = RoutePath;

  menuItems = MENU_ITEMS;
}
