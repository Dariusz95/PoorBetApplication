import { Component, contentChild, HostBinding, input } from '@angular/core';
import { PbCardHeaderDirective } from './pb-card-header.directive';

@Component({
  selector: 'app-pb-card',
  templateUrl: './pb-card.component.html',
  styleUrl: './pb-card.component.scss',
})
export class PbCardComponent {
  stylesCss = input<string | null>(null);
  variant = input<'default' | 'highlighted'>('default');

  header = contentChild(PbCardHeaderDirective);

  @HostBinding('class') get class() {
    return this.stylesCss();
  }
}
