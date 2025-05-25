import { CommonModule } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  contentChild,
  DestroyRef,
  forwardRef,
  inject,
  Injector,
  input,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  ControlValueAccessor,
  FormControl,
  FormControlDirective,
  FormControlName,
  FormGroupDirective,
  FormsModule,
  NG_VALUE_ACCESSOR,
  NgControl,
  NgModel,
  ReactiveFormsModule,
} from '@angular/forms';
import { tap } from 'rxjs';
import { PbErrorsComponent } from '../pb-errors/pb-errors.component';
import { PbLabel } from './directives/pb-label';

@Component({
  selector: 'pb-form-field',
  imports: [CommonModule, FormsModule, ReactiveFormsModule, PbErrorsComponent],
  standalone: true,
  templateUrl: './pb-form-field.component.html',
  styleUrl: './pb-form-field.component.scss',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => PbFormFieldComponent),
      multi: true,
    },
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PbFormFieldComponent implements ControlValueAccessor {
  private readonly injector = inject(Injector);
  private readonly destroyRef = inject(DestroyRef);

  readonly labelRef = contentChild(PbLabel);

  placeholder = input<string>('');
  type = input<string>('text');
  disabled = signal<boolean>(false);

  value = signal<string>('');
  touched = signal<boolean>(false);
  focused = signal<boolean>(false);
  control: FormControl | null = null;

  ngOnInit() {
    this.setComponentControl();
  }

  onChange = (_: any) => {};
  onTouched = () => {};

  writeValue(value: any): void {
    this.value.set(value || '');
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled.set(isDisabled);
  }

  handleInput(event: Event): void {
    const val = (event.target as HTMLInputElement).value;
    this.value.set(val);
    this.onChange(val);
  }

  handleFocus(): void {
    this.focused.set(true);
  }

  handleBlur(): void {
    this.onTouched();
    this.touched.set(true);
    this.focused.set(false);
  }

  private setComponentControl<T>(): void {
    const injectedControl = this.injector.get(NgControl);

    switch (injectedControl.constructor) {
      case NgModel: {
        const { control, update } = injectedControl as NgModel;

        this.control = control;

        this.control.valueChanges
          .pipe(
            tap((value: T) => update.emit(value)),
            takeUntilDestroyed(this.destroyRef)
          )
          .subscribe();
        break;
      }
      case FormControlName: {
        this.control = this.injector
          .get(FormGroupDirective)
          .getControl(injectedControl as FormControlName);
        break;
      }
      default: {
        this.control = (injectedControl as FormControlDirective)
          .form as FormControl;
        break;
      }
    }
  }
}
