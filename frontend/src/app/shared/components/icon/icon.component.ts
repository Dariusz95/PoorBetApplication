import { CommonModule } from '@angular/common';
import { Component, computed, input } from '@angular/core';

type IconSize = 's' | 'm' | 'lg';
type IconColor = 'primary' | 'success' | 'danger' | 'warning';

@Component({
  selector: 'app-icon',
  standalone: true,
  template: `
    <span
      class="material-icons"
      [attr.aria-hidden]="true"
      [class]="sizeClass() + ' ' + colorClass()"
      >{{ icon() }}</span
    >
  `,
  styles: [
    `
      :host {
        @apply inline-flex justify-center items-center;
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
  imports: [CommonModule],
})
export class IconComponent {
  icon = input.required<string>();
  size = input<IconSize>('m');
  color = input<IconColor>('primary');

  sizeClass = computed(() => `icon-${this.size()}`);

  colorClass = computed(() => {
    switch (this.color()) {
      case 'success':
        return 'text-green-500';
      case 'danger':
        return 'text-red-500';
      case 'warning':
        return 'text-yellow-500';
      case 'primary':
        return 'text-app-primary';
      default:
        return 'text-blue-500';
    }
  });
}
