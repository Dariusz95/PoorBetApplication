import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Output,
} from '@angular/core';
import { TranslocoPipe } from '@jsverse/transloco';
import { PbButtonComponent } from '../../../../../shared/ui/pb-button/pb-button.component';

@Component({
  selector: 'app-coupon-footer',
  standalone: true,
  imports: [TranslocoPipe, PbButtonComponent],
  template: `
    <footer class="coupon__footer">
      <pb-button (buttonClick)="onClose()">
        {{ 'coupon.close' | transloco }}
      </pb-button>
    </footer>
  `,
  styleUrl: './coupon-footer.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CouponFooterComponent {
  @Output() close = new EventEmitter<void>();

  onClose(): void {
    this.close.emit();
  }
}
