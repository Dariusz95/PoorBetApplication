import { Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { filter, map, startWith } from 'rxjs';
import { HeaderComponent } from '../../components/header/header.component';
import { MobileMenuComponent } from '../../components/mobile-header/mobile-menu.component';
import { RouteFragment } from '../../routing/route-fragment';

@Component({
  selector: 'app-layout',
  imports: [RouterOutlet, HeaderComponent, MobileMenuComponent],
  templateUrl: './app-layout.component.html',
  styleUrls: ['./app-layout.component.scss'],
})
export class AppLayoutComponent {
  private readonly router = inject(Router);

  private readonly url = toSignal(
    this.router.events.pipe(
      filter((event): event is NavigationEnd => event instanceof NavigationEnd),
      map((event) => event.urlAfterRedirects),
      startWith(this.router.url),
    ),
    { initialValue: this.router.url },
  );

  readonly isAuthRoute = computed(() =>
    this.url().startsWith(`/${RouteFragment.Auth}`),
  );
}
