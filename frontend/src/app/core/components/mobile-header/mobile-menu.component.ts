import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TranslocoPipe } from '@jsverse/transloco';

// Dane dla elementów menu mobilnego (ikona + tekst)
interface MobileMenuItem {
  labelKey: string; // Klucz do tłumaczenia
  link: string;
  iconName: string; // Nazwa ikony (np. dla Material Icons)
}

@Component({
  selector: 'app-mobile-menu',
  standalone: true,
  imports: [CommonModule, RouterLink, TranslocoPipe],
  templateUrl: './mobile-menu.component.html',
  styleUrls: ['./mobile-menu.component.scss'],
})
export class MobileMenuComponent {
  // Elementy menu i autentykacji dla paska na dole
  menuItems: MobileMenuItem[] = [
    { labelKey: 'menu.home', link: '/', iconName: 'home' },
    {
      labelKey: 'menu.transactions',
      link: '/transactions',
      iconName: 'receipt_long',
    }, // Przykładowa ścieżka z obrazka
    { labelKey: 'menu.picks', link: '/picks', iconName: 'how_to_vote' }, // Przykładowa ścieżka z obrazka
    { labelKey: 'menu.settings', link: '/settings', iconName: 'settings' }, // Przykładowa ścieżka z obrazka
    // Możesz tu też dodać "Log out" i "Help Center" jeśli mają być na dole
    // { labelKey: 'menu.logout', link: '/auth/logout', iconName: 'logout' },
    // { labelKey: 'menu.help', link: '/help', iconName: 'help_outline' },
  ];

  constructor() {}
}
