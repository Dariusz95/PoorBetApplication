import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { PbButtonComponent } from '../../../../shared/components/pb-button/pb-button.component';

@Component({
  selector: 'app-odds-button',
  imports: [PbButtonComponent],
  templateUrl: './odds-button.component.html',
  styleUrl: './odds-button.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OddsButtonComponent {
  label = input.required<string>();
  value = input.required<number>();
  selected = input(false);

  oddsClick = output<void>();
}
