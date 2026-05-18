import { Component, contentChild, HostBinding, input } from '@angular/core';
import { PbCardHeaderDirective } from './directives/pb-card-header.directive';

@Component({
  selector: 'pb-card',
  templateUrl: './pb-card.component.html',
  styleUrl: './pb-card.component.scss',
  standalone: true,
})
export class PbCardComponent {
  stylesCss = input<string | null>(null);
  variant = input<'default' | 'highlighted'>('default');

  header = contentChild(PbCardHeaderDirective);

  @HostBinding('class') get class() {
    return this.stylesCss();
  }
}
