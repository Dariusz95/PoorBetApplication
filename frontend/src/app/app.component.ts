import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './core/components/header/header.component';
import { MobileMenuComponent } from './core/components/mobile-header/mobile-menu.component';
import { BetPageComponent } from "./core/bet/services/bet-page/bet-page.component";


@Component({
  selector: 'app-root',
  imports: [RouterOutlet, HeaderComponent, MobileMenuComponent, BetPageComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {


  ngOnInit() {
    console.log('d1dx');
  }
}
