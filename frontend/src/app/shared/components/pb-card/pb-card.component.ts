import { Component, HostBinding, input } from '@angular/core';

@Component({
  selector: 'app-pb-card',
  imports: [],
  template: `<ng-content></ng-content>`,
  styleUrl: './pb-card.component.scss',
})
export class PbCardComponent {
  stylesCss = input<string | null>(null);

  @HostBinding('class') get class() {
    return this.stylesCss();
  }
}
