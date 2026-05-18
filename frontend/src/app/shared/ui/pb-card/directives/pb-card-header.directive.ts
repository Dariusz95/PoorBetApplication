import { Directive } from '@angular/core';

@Directive({
  selector: '[pbCardHeader]',
  host: {
    class: 'pb-card-section',
  },
})
export class PbCardHeaderDirective {}
