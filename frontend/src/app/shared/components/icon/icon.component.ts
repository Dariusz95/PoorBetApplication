import { Component, computed, input } from '@angular/core';

export type IconSize = 's' | 'm' | 'lg';

@Component({
  selector: 'app-icon',
  standalone: true,
  template: `
    <span class="material-icons" [class]="sizeClass()">{{ icon() }}</span>
  `,
  styles: [
    `
      :host {
        @apply flex justify-center items-center;
      }

      .icon-s {
        font-size: 16px;
      }
      .icon-m {
        font-size: 24px;
      }
      .icon-lg {
        font-size: 32px;
      }
    `,
  ],
})
export class IconComponent {
  icon = input.required<string>();
  size = input<IconSize>('m');

  sizeClass = computed(() => `icon-${this.size()}`);
}
