import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  contentChild,
  DestroyRef,
  inject,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { PbErrorsComponent } from '../pb-errors/pb-errors.component';
import { ErrorValueMap } from '../pb-errors/types/error-value-map';
import { FORM_FIELD_CONTROL } from './directives/pb-form-field-control';
import { PbLabel } from './directives/pb-label';

@Component({
  selector: 'pb-form-field',
  imports: [FormsModule, ReactiveFormsModule, PbErrorsComponent],
  templateUrl: './pb-form-field.component.html',
  styleUrl: './pb-form-field.component.scss',

  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PbFormFieldComponent {
  private readonly _cdr = inject(ChangeDetectorRef);
  private readonly destroyRef = inject(DestroyRef);

  control = contentChild.required(FORM_FIELD_CONTROL);

  labelRef = contentChild(PbLabel);

  disabled = signal(false);
  focused = signal(false);
  errors = signal<ErrorValueMap | null>(null);
  value = signal<string>('');
  touched = signal(false);
  invalid = signal(false);
  _labelId = crypto.randomUUID();

  ngAfterContentInit(): void {
    this.control().labelId?.set(this._labelId);

    this.control()
      .stateChanges.pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this._updateFocusState();
        this._updateErrorsState();
        this._updateTouchedState();
        this._updateInvalidState();

        this._cdr.markForCheck();
      });
  }

  private _updateInvalidState(): void {
    this.invalid.set(!!this.control().errors());
  }

  private _updateFocusState(): void {
    this.focused.set(this.control().focused());
  }

  private _updateTouchedState(): void {
    this.touched.set(this.control().touched());
    if (this.control().touched()) {
      this.touched.set(true);
    }
  }

  private _updateErrorsState(): void {
    const errors = this.control().errors();

    if (!errors) {
      this.errors.set(null);

      return;
    }

    this.errors.set(errors);
  }
}
