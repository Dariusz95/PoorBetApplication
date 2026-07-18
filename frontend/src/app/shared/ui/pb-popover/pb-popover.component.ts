import {
  ChangeDetectionStrategy,
  Component,
  input,
  signal,
  viewChild,
} from '@angular/core';

import {
  animate,
  state,
  style,
  transition,
  trigger,
} from '@angular/animations';
import { A11yModule } from '@angular/cdk/a11y';
import {
  CdkOverlayOrigin,
  ConnectedPosition,
  OverlayModule,
} from '@angular/cdk/overlay';
import { CommonModule } from '@angular/common';

const dropdownAnimation = trigger('dropdownAnimation', [
  state(
    'void',
    style({ opacity: 0, transform: 'translateY(-4px) scale(0.98)' }),
  ),
  state('*', style({ opacity: 1, transform: 'translateY(0) scale(1)' })),
  transition(':enter', [animate('120ms ease-out')]),
  transition(':leave', [animate('90ms ease-in')]),
]);

export const popoverAnimation = trigger('popoverAnimation', [
  transition(':enter', [
    style({
      opacity: 0,
      transform: 'scale(0.95)',
    }),
    animate(
      '150ms ease-out',
      style({
        opacity: 1,
        transform: 'scale(1)',
      }),
    ),
  ]),
  transition(':leave', [
    animate(
      '100ms ease-in',
      style({
        opacity: 0,
        transform: 'scale(0.95)',
      }),
    ),
  ]),
]);

@Component({
  selector: 'pb-popover',
  imports: [OverlayModule, A11yModule, CommonModule],
  templateUrl: './pb-popover.component.html',
  styleUrl: './pb-popover.component.scss',
  standalone: true,
  animations: [popoverAnimation],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PbPopoverComponent {
  private readonly triggerOrigin = viewChild<CdkOverlayOrigin>('trigger');

  origin = input<CdkOverlayOrigin | undefined>();
  width = input<string>('180px');

  positions = input<ConnectedPosition[]>([
    {
      originX: 'end',
      originY: 'bottom',
      overlayX: 'end',
      overlayY: 'top',
    },
  ]);

  isOpen = signal(false);

  toggle(): void {
    if (this.isOpen()) {
      this.close();
    } else {
      this.open();
    }
  }

  open(): void {
    this.isOpen.set(true);
  }

  close(): void {
    this.isOpen.set(false);
    this.triggerOrigin()?.elementRef.nativeElement.focus();
  }
}
