import { Component, computed, input } from '@angular/core';
import { BetStatus } from '@features/coupons/types/bet-status';
import { TranslocoPipe } from '@jsverse/transloco';
import { BET_TYPE_TO_OPTION } from '@shared/types/bet-option';
import { BetType } from '@shared/types/bet-type';

@Component({
  selector: 'app-bet-type-label',
  imports: [TranslocoPipe],
  template: `
    <p
      class="bet-type"
      [class.bet-type--won]="betStatus() === 'WON'"
      [class.bet-type--lost]="betStatus() === 'LOST'"
    >
      {{ 'coupon.match' | transloco }}: {{ label() }}
    </p>
  `,
  styleUrl: './bet-type-label.component.scss',
  standalone: true,
})
export class BetTypeLabelComponent {
  betType = input.required<BetType>();
  betStatus = input<BetStatus>();

  label = computed(() => BET_TYPE_TO_OPTION[this.betType()] ?? this.betType());
}
