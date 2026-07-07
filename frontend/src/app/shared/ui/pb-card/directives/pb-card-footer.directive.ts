import { Directive } from '@angular/core';

@Directive({
  selector: '[pbCardFooter]',
  host: {
    class: 'pb-card-section',
  },
})
export class PbCardFooterDirective {}
