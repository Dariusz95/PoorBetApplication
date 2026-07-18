import { Directive, ElementRef } from "@angular/core";

@Directive({
  selector: '[pbInputIconRight]',
})
export class PbInputIconRightDirective {
  constructor(public el: ElementRef) {}
}
