import { Component, OnInit } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { CouponListComponent } from '@features/coupons/components/coupon-list/coupon-list.component';
import { PbIconComponent } from '@shared/ui/icon/pb-icon.component';
import { PbPopoverComponent } from '@shared/ui/pb-popover/pb-popover.component';
import { ImageType } from '../../../shared/ui/pb-image/image-type.model';

@Component({
  selector: 'app-coupon-dropdown',
  imports: [
    ReactiveFormsModule,
    CouponListComponent,
    PbPopoverComponent,
    PbIconComponent,
  ],
  templateUrl: './coupon-dropdown.component.html',
  styleUrls: ['./coupon-dropdown.component.scss'],
})
export class CouponDropdownComponent implements OnInit {
  readonly ImageType = ImageType;

  ngOnInit(): void {}
}
