import {
  ChangeDetectionStrategy,
  Component,
  contentChild,
  effect,
  forwardRef,
  inject,
  Injector,
  input,
  signal,
} from '@angular/core';
import {
  ControlValueAccessor,
  NG_VALUE_ACCESSOR,
  NgControl,
} from '@angular/forms';
import { Subject } from 'rxjs';
import { ErrorValueMap } from '../pb-errors/types/error-value-map';
import {
  FORM_FIELD_CONTROL,
  FormFieldControl,
} from '../pb-form-field/directives/pb-form-field-control';
import { PbInputIconDirective } from './directives/pb-input-icon.directive';

@Component({
  selector: 'pb-input',
  standalone: true,
  templateUrl: './pb-input.component.html',
  styleUrls: ['./pb-input.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => PbInputComponent),
      multi: true,
    },
    {
      provide: FORM_FIELD_CONTROL,
      useExisting: forwardRef(() => PbInputComponent),
    },
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PbInputComponent
  implements ControlValueAccessor, FormFieldControl
{
  placeholder = input<string>('');
  type = input<string>('text');

  icon = contentChild(PbInputIconDirective);

  private readonly injector = inject(Injector);

  private ngControl: NgControl | null = null;
  readonly stateChanges: Subject<void> = new Subject<void>();

  errors = signal<ErrorValueMap | null>(null);
  disabled = signal<boolean>(false);
  focused = signal<boolean>(false);
  touched = signal<boolean>(false);
  invalid = signal<boolean>(false);
  value = signal<string>('');

  onChange = (_: any) => {};
  onTouched = () => {};

  get hasIcon(): boolean {
    return !!this.icon();
  }

  constructor() {
    effect(() => {
      this.focused();
      this.touched();
      this.errors();
      this.disabled();
      this.setErrors(this.ngControl?.errors as ErrorValueMap | null);
      this.stateChanges.next();
    });
  }

  ngOnInit() {
    this.ngControl = this.injector.get(NgControl);
  }

  setErrors(errors: ErrorValueMap | null) {
    this.errors.set(errors);
    this.invalid.set(!!errors);
  }

  handleInput(event: Event): void {
    const val = (event.target as HTMLInputElement).value;

    this.value.set(val);
    this.onChange(val);
  }

  onFocus(): void {
    this.focused.set(true);
  }

  onBlur(): void {
    this.focused.set(false);
    this.touched.set(true);
    this.onTouched();
  }

  writeValue(val: string): void {
    this.value.set(val || '');
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }
}
