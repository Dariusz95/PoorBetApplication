import { Directive } from '@angular/core';

@Directive({
  selector: '[pbCardBody]',
  host: {
    class: 'pb-card-section',
  },
})
export class PbCardBodyDirective {}
