import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';

export interface SummaryRow {
  label: string;
  value: string | number;
  isHighlight?: boolean;
}

@Component({
  selector: 'app-coupon-summary-row',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="coupon-summary-row">
      <span class="coupon-summary-row__label">
        <ng-content select="[summaryLabel]" />
      </span>

      <span class="coupon-summary-row__value">
        <ng-content select="[summaryValue]" />
      </span>
    </div>
  `,
  styleUrl: './coupon-summary-row.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CouponSummaryRowComponent {}
