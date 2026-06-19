import { Component } from '@angular/core';
import { PbIconComponent } from '@shared/ui/icon/pb-icon.component';
import { PbPopoverComponent } from '@shared/ui/pb-popover/pb-popover.component';
import { UserBalanceComponent } from '../user-balance/user-balance.component';
import { UserDropdownContentComponent } from './user-dropdown-content/user-dropdown-content.component';

@Component({
  selector: 'app-user-dropdown',
  imports: [
    PbPopoverComponent,
    PbIconComponent,
    UserBalanceComponent,
    UserDropdownContentComponent,
  ],
  templateUrl: './user-dropdown.component.html',
  styleUrl: './user-dropdown.component.scss',
})
export class UserDropdownComponent {}
