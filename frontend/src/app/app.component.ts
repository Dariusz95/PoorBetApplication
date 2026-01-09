import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { BetPageComponent } from './core/bet/bet-page/bet-page.component';
import { HeaderComponent } from './core/components/header/header.component';
import { MobileMenuComponent } from './core/components/mobile-header/mobile-menu.component';

@Component({
  selector: 'app-root',
  imports: [
    RouterOutlet,
    HeaderComponent,
    MobileMenuComponent,
    BetPageComponent,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  ngOnInit() {
    console.log('d1dx');
  }
}
