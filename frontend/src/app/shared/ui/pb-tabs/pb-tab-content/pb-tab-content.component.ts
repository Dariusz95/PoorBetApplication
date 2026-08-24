import { Component, Input, signal } from '@angular/core';

@Component({
  selector: 'pb-tab-content',
  standalone: true,
  templateUrl: './pb-tab-content.component.html',
  host: {
    role: 'tabpanel',
    '[id]': "'pb-tabpanel-' + value",
    '[attr.aria-labelledby]': 'tabId()',
  },
})
export class PbTabContentComponent<T = string> {
  @Input() value!: T;
  active = signal(false);

  tabId(): string {
    return `pb-tab-${this.value}`;
  }
}
