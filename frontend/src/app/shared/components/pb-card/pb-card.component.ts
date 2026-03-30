import { Component, HostBinding, input } from '@angular/core';

@Component({
  selector: 'app-pb-card',
  imports: [],
  templateUrl: './pb-card.component.html',
  styleUrl: './pb-card.component.scss',
})
export class PbCardComponent {
  stylesCss = input<string | null>(null);

  @HostBinding('class') get class() {
    return this.stylesCss();
  }
}
