import {
  ChangeDetectionStrategy,
  Component,
  forwardRef,
  input,
  signal,
} from '@angular/core';

import {
  animate,
  state,
  style,
  transition,
  trigger,
} from '@angular/animations';
import {
  CdkOverlayOrigin,
  ConnectedPosition,
  OverlayModule,
} from '@angular/cdk/overlay';
import { CommonModule } from '@angular/common';
import { TemplateRef } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { TranslocoPipe } from '@jsverse/transloco';
import { DropdownOption } from './dropdown-option';

const dropdownAnimation = trigger('dropdownAnimation', [
  state('void', style({ opacity: 0, transform: 'scale(0.95)' })),
  state('*', style({ opacity: 1, transform: 'scale(1)' })),
  transition(':enter', [animate('100ms ease-out')]),
  transition(':leave', [animate('75ms ease-in')]),
]);

@Component({
  selector: 'app-pb-dropdown',
  imports: [OverlayModule, CommonModule, TranslocoPipe],
  templateUrl: './pb-dropdown.component.html',
  styleUrl: './pb-dropdown.component.scss',
  standalone: true,
  animations: [dropdownAnimation],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => PbDropdownComponent),
      multi: true,
    },
  ],
})
export class PbDropdownComponent<T extends DropdownOption>
  implements ControlValueAccessor
{
  options = input<T[]>([]);
  origin = input<CdkOverlayOrigin | undefined>(undefined);
  positions = input<ConnectedPosition[]>([
    { originX: 'start', originY: 'bottom', overlayX: 'start', overlayY: 'top' },
    { originX: 'start', originY: 'top', overlayX: 'start', overlayY: 'bottom' },
  ]);
  optionTemplate = input<TemplateRef<{ $implicit: T }> | undefined>(undefined);
  closeOnSelect = input(true);

  isOpen = signal(false);
  value = signal<string | null>(null);

  private onChange: (value: string | null) => void = () => {};
  private onTouched: () => void = () => {};

  writeValue(value: string | null): void {
    this.value.set(value);
  }
  registerOnChange(fn: (value: string | null) => void): void {
    this.onChange = fn;
  }
  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

  toggleDropdown(): void {
    this.isOpen.update((open) => !open);
  }

  closeDropdown(): void {
    this.isOpen.set(false);
    this.onTouched();
  }

  selectOption(option: T): void {
    this.value.set(option.value);
    this.onChange(option.value);
    if (this.closeOnSelect()) this.closeDropdown();
  }

  selectedOption(): T | undefined {
    return this.options().find((o) => o.value === this.value());
  }
}
