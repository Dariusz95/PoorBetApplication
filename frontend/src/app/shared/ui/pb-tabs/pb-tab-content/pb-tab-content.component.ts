import { Component, Input, signal } from '@angular/core';

@Component({
  selector: 'pb-tab-content',
  standalone: true,
  templateUrl: './pb-tab-content.component.html',
})
export class PbTabContentComponent<T = string> {
  @Input() value!: T;
  active = signal(false);
}
