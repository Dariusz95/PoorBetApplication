import { CommonModule } from '@angular/common';
import {
  booleanAttribute,
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  input,
  Output,
} from '@angular/core';
import { RouterLink } from '@angular/router';
import { TranslocoPipe } from '@jsverse/transloco';

@Component({
  selector: 'pb-button',
  standalone: true,
  imports: [CommonModule, RouterLink, TranslocoPipe],
  templateUrl: './pb-button.component.html',
  styleUrls: ['./pb-button.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PbButtonComponent {
  type = input<string>('primary');
  text = input<string>('');

  routerLink = input<string | any[] | null | undefined>(undefined);

  href = input<string | null | undefined>(undefined);

  disabled = input(false, { transform: booleanAttribute });
  htmlType = input<'button' | 'submit' | 'reset'>('button');

  ariaLabel = input<string | undefined>(undefined);

  @Output() click = new EventEmitter<Event>();

  get renderAs(): 'link' | 'button' {
    return this.routerLink() || this.href() ? 'link' : 'button';
  }

  onClick(event: Event): void {
    if (!this.disabled()) {
      this.click.emit(event);
    }
    if (this.disabled()) {
      event.preventDefault();
      event.stopPropagation();
    }
  }
}
