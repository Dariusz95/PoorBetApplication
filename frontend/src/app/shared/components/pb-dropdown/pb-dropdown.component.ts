import { ChangeDetectionStrategy, Component } from '@angular/core';

import {
  animate,
  state,
  style,
  transition,
  trigger,
} from '@angular/animations';
import {
  CdkOverlayOrigin,
  ConnectedPosition,
  OverlayModule,
} from '@angular/cdk/overlay';
import { CommonModule } from '@angular/common';
import {
  EventEmitter,
  Input,
  Output,
  SimpleChanges,
  TemplateRef,
} from '@angular/core';

const dropdownAnimation = trigger('dropdownAnimation', [
  state('void', style({ opacity: 0, transform: 'scale(0.95)' })),
  state('*', style({ opacity: 1, transform: 'scale(1)' })),
  transition(':enter', [animate('100ms ease-out')]),
  transition(':leave', [animate('75ms ease-in')]),
]);

@Component({
  selector: 'app-pb-dropdown',
  imports: [OverlayModule, CommonModule],
  templateUrl: './pb-dropdown.component.html',
  styleUrl: './pb-dropdown.component.scss',
  standalone: true,
  animations: [dropdownAnimation],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PbDropdownComponent {
  @Input() options: any[] = [];
  @Input() optionLabelKey: string = 'label';
  @Input() origin?: CdkOverlayOrigin;
  @Input() positions: ConnectedPosition[] = [];

  @Output() optionSelected = new EventEmitter<any>();

  isOpen = false;

  @Input() optionTemplate?: TemplateRef<any>;

  @Input() triggerTemplate?: TemplateRef<any>;

  constructor() {
    if (this.positions.length === 0) {
      this.positions = [
        {
          originX: 'start',
          originY: 'bottom',
          overlayX: 'start',
          overlayY: 'top',
        },
        {
          originX: 'start',
          originY: 'top',
          overlayX: 'start',
          overlayY: 'bottom',
        },
      ];
    }
  }

  ngOnInit(): void {}

  ngOnChanges(changes: SimpleChanges): void {}

  toggleDropdown(): void {
    this.isOpen = !this.isOpen;
  }

  closeDropdown(): void {
    this.isOpen = false;
  }

  selectOption(option: any): void {
    this.optionSelected.emit(option);
    this.closeDropdown();
  }
}
