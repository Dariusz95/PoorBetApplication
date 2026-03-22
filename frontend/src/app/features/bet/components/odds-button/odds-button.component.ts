import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { PbButtonComponent } from '../../../../shared/components/pb-button/pb-button.component';

@Component({
  selector: 'app-odds-button',
  imports: [PbButtonComponent],
  templateUrl: './odds-button.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OddsButtonComponent {
  label = input.required<string>();
  value = input.required<number>();
}
