import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from '../../components/header/header.component';
import { MobileMenuComponent } from '../../components/mobile-header/mobile-menu.component';

@Component({
  selector: 'app-layout',
  imports: [RouterOutlet, HeaderComponent, MobileMenuComponent],
  templateUrl: './app-layout.component.html',
})
export class AppLayoutComponent {}
