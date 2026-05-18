import { Component } from '@angular/core';
import { TranslocoPipe } from '@jsverse/transloco';

@Component({
  selector: 'app-coupon-empty',
  imports: [TranslocoPipe],
  template: `
    <div class="empty-section">
      <p class="t-heading-sm">{{ 'bet.coupon.emptyTitle' | transloco }}</p>
      <p class="t-body-sm t-muted">{{ 'bet.coupon.emptyText' | transloco }}</p>
    </div>
  `,
  styles: `
    .empty-section {
      padding: 1rem;
      border-radius: var(--radius-md);
      border: 1px dashed var(--color-border);
      background-color: var(--color-surface-soft);
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
    }
  `,
})
export class EmptyCouponComponent {}
