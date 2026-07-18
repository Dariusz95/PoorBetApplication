import { CommonModule } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  computed,
  contentChild,
  inject,
  input,
  output,
} from '@angular/core';
import { RouterLink } from '@angular/router';
import { RouteParam } from '../../../core/routing/route-param';
import { RoutePath } from '../../../core/routing/route-path';
import { RoutingService } from '../../../core/routing/routing.service';
import { PbIconComponent } from '../icon/pb-icon.component';
import { PbSpinnerComponent } from '../pb-spinner/pb-spinner.component';
import { ButtonSize, ButtonVariant } from './pb-button.model';

@Component({
  selector: 'pb-button',
  standalone: true,
  imports: [CommonModule, RouterLink, PbSpinnerComponent],
  templateUrl: './pb-button.component.html',
  styleUrls: ['./pb-button.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PbButtonComponent {
  private readonly routingService = inject(RoutingService);

  variant = input<ButtonVariant>('primary');
  size = input<ButtonSize>('responsive');
  routerPath = input<RoutePath | null>(null);
  routerParams = input<Partial<Record<RouteParam, string>> | null>(null);
  disabled = input<boolean>(false);
  type = input<'button' | 'submit' | 'reset' | 'link' | 'icon'>('button');
  fullWidth = input<boolean>(false);
  loading = input<boolean>(false);
  ariaLabel = input<string | undefined>();
  testId = input<string | undefined>(undefined);

  buttonClick = output<Event>();

  iconTemplate = contentChild(PbIconComponent);

  routePath = computed(() => {
    if (!this.routerPath()) {
      return null;
    }

    return this.routingService.createLink(
      this.routerPath()!,
      this.routerParams(),
    );
  });

  onClick(event: Event): void {
    if (this.loading() || this.disabled()) {
      event.preventDefault();
      return;
    }

    this.buttonClick.emit(event);
  }

  getButtonClasses(): string {
    const classes = [
      'pb-button',
      `pb-button--${this.variant()}`,
      `pb-button--${this.size()}`,
    ];

    if (this.fullWidth()) classes.push('pb-button--full-width');
    if (this.loading()) classes.push('pb-button--loading');
    if (this.iconTemplate() && this.type() === 'icon')
      classes.push('pb-button--icon');

    return classes.join(' ');
  }
}
