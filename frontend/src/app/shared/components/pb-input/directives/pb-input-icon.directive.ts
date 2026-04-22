import { Directive, ElementRef } from "@angular/core";

@Directive({
  selector: '[pbInputIcon]',
})
export class PbInputIconDirective {
  constructor(public el: ElementRef) {}
}
