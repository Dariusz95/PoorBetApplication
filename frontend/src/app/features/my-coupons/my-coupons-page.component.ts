import { Component } from '@angular/core';
import { TranslocoDirective } from '@jsverse/transloco';

@Component({
  selector: 'app-my-coupons-page',
  standalone: true,
  imports: [TranslocoDirective],
  templateUrl: './my-coupons-page.component.html',
})
export class MyCouponsPageComponent {}
