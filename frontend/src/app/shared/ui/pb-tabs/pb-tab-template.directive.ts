import { Directive, Input, TemplateRef } from '@angular/core';

export interface TabContext<T> {
  $implicit: T;
}

@Directive({
  selector: '[appTabTemplate]',
})
export class TabTemplateDirective<T> {
  @Input('appTabTemplate') data!: T;

  constructor(public template: TemplateRef<TabContext<T>>) {}

  static ngTemplateContextGuard<T>(
    dir: TabTemplateDirective<T>,
    ctx: unknown,
  ): ctx is TabContext<T> {
    return true;
  }
}
