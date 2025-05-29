import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './core/components/header/header.component';
import { MobileMenuComponent } from './core/components/mobile-header/mobile-menu.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, HeaderComponent, MobileMenuComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  constructor() {}

  ngOnInit() {
    console.log('d1dx');
  }
}
