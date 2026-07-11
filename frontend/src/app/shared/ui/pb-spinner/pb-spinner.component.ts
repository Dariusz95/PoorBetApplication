import { CommonModule } from '@angular/common';
import { Component, computed, input } from '@angular/core';

type SpinnerSize = 's' | 'm' | 'lg';

@Component({
  selector: 'pb-spinner',
  standalone: true,
  template: `
    <span class="pb-spinner {{ sizeClass() }}" role="status" [attr.aria-label]="ariaLabel()"></span>
  `,
  styles: [
    `
      .pb-spinner {
        display: inline-block;
        border-radius: 50%;
        border: 2px solid color-mix(in srgb, currentColor 25%, transparent);
        border-top-color: currentColor;
        animation: pb-spinner-rotate 0.7s linear infinite;
      }

      .pb-spinner-s {
        width: 16px;
        height: 16px;
      }

      .pb-spinner-m {
        width: 24px;
        height: 24px;
      }

      .pb-spinner-lg {
        width: 32px;
        height: 32px;
        border-width: 3px;
      }

      @keyframes pb-spinner-rotate {
        to {
          transform: rotate(360deg);
        }
      }

      @media (prefers-reduced-motion: reduce) {
        .pb-spinner {
          animation-duration: 1.4s;
        }
      }
    `,
  ],
  imports: [CommonModule],
})
export class PbSpinnerComponent {
  size = input<SpinnerSize>('m');
  ariaLabel = input<string>('Loading');

  sizeClass = computed(() => `pb-spinner-${this.size()}`);
}
