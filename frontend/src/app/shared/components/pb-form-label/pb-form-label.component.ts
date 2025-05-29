import { Component, HostBinding, input } from '@angular/core';

@Component({
  selector: 'app-pb-form-label',
  imports: [],
  template: `<ng-content></ng-content>`,
  standalone: true,
  styleUrl: './pb-form-label.component.scss',
})
export class PbFormLabelComponent {
  invalid = input<boolean>(false);
  focused = input<boolean>(false);

  @HostBinding('class.invalid') get isInvalid() {
    return this.invalid();
  }

  @HostBinding('class.focused') get isFocused() {
    return this.focused();
  }
}
