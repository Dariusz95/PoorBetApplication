
import { TranslocoPipe } from '@jsverse/transloco';
import { MENU_ITEMS } from './models/menu-items';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-mobile-menu',
  standalone: true,
  imports: [RouterLink, TranslocoPipe],
  templateUrl: './mobile-menu.component.html',
  styleUrls: ['./mobile-menu.component.scss'],
})
export class MobileMenuComponent {
  menuItems = MENU_ITEMS;

  constructor() {}
}
