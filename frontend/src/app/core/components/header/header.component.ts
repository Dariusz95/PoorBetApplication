import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TranslocoPipe } from '@jsverse/transloco';
import { LanguageSwitcherComponent } from '../../../shared/components/language-switcher/language-switcher.component';
import { PbButtonComponent } from '../../../shared/components/pb-button/pb-button.component';
import { ThemeToggleComponent } from '../../../shared/components/theme-toggle/theme-toggle.component';

interface MenuItem {
  labelKey: string;
  link: string;
}

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    TranslocoPipe,
    PbButtonComponent,
    ThemeToggleComponent,
    LanguageSwitcherComponent,
  ],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent {
  menuItems: MenuItem[] = [
    { labelKey: 'menu.home', link: '/' },
    { labelKey: 'menu.about', link: '/about' },
    { labelKey: 'menu.contact', link: '/contact' },
  ];
}
