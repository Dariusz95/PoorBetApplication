import { CommonModule } from '@angular/common';
import { Component, input } from '@angular/core';

@Component({
  selector: 'pb-skeleton',
  standalone: true,
  template: `
    <span
      class="pb-skeleton"
      [style.width]="width()"
      [style.height]="height()"
      [style.border-radius]="borderRadius()"
      aria-hidden="true"
    ></span>
  `,
  styles: [
    `
      .pb-skeleton {
        display: block;
        background: linear-gradient(
          90deg,
          var(--color-surface-soft) 25%,
          var(--color-border) 37%,
          var(--color-surface-soft) 63%
        );
        background-size: 400% 100%;
        animation: pb-skeleton-shimmer 1.4s ease-in-out infinite;
      }

      @keyframes pb-skeleton-shimmer {
        0% {
          background-position: 100% 50%;
        }
        100% {
          background-position: 0 50%;
        }
      }

      @media (prefers-reduced-motion: reduce) {
        .pb-skeleton {
          animation: none;
        }
      }
    `,
  ],
  imports: [CommonModule],
})
export class PbSkeletonComponent {
  width = input<string>('100%');
  height = input<string>('1rem');
  borderRadius = input<string>('0.5rem');
}
