import { CommonModule } from '@angular/common';
import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ContentChild,
  contentChild,
  inject,
  signal,
} from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { PbErrorsComponent } from '../pb-errors/pb-errors.component';
import { ErrorValueMap } from '../pb-errors/types/error-value-map';
import {
  FORM_FIELD_CONTROL,
  FormFieldControl,
} from './directives/pb-form-field-control';
import { PbLabel } from './directives/pb-label';

@Component({
  selector: 'pb-form-field',
  imports: [CommonModule, FormsModule, ReactiveFormsModule, PbErrorsComponent],
  standalone: true,
  templateUrl: './pb-form-field.component.html',
  styleUrl: './pb-form-field.component.scss',

  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PbFormFieldComponent {
  private readonly _changeDetectorRef = inject(ChangeDetectorRef);

  @ContentChild(FORM_FIELD_CONTROL) control!: FormFieldControl;

  labelRef = contentChild(PbLabel);

  disabled = signal(false);
  focused = signal(false);
  errors = signal<ErrorValueMap | null>(null);
  value = signal<string>('');
  touched = signal(false);
  invalid = signal(false);
  _labelId ='';

  ngAfterContentInit() {
    this.control.stateChanges.subscribe(() => {
      this._updateFocusState();
      this._updateErrorsState();
      this._updateTouchedState();
      this._updateInvalidState();
      console.log(this.control.errors());

      this._changeDetectorRef.markForCheck();
    });
  }

  private _updateInvalidState() {
    this.invalid.set(!!this.control.errors());
  }

  private _updateFocusState() {
    this.focused.set(this.control.focused());
  }

  private _updateTouchedState() {
    this.touched.set(this.control.touched());
    if (this.control.touched()) {
      this.touched.set(true);
    }
  }

  private _updateErrorsState() {
    const errors = this.control.errors();
    console.log('Errors:', errors);
    if (!errors) {
      this.errors.set(null);
      return;
    }

    this.errors.set(errors as ErrorValueMap);
  }
}
