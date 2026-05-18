import { DialogRef } from '@angular/cdk/dialog';
import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

@Component({
  selector: 'pb-dialog',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="pb-dialog">
      <div class="pb-dialog-header">
        <ng-content select="[pbDialogHeader]"></ng-content>
      </div>
      <div class="pb-dialog-body">
        <ng-content select="[pbDialogBody]"></ng-content>
      </div>
      <div class="pb-dialog-footer">
        <ng-content select="[pbDialogFooter]"></ng-content>
      </div>
    </div>
  `,
  styles: [
    `
      .pb-dialog {
        display: flex;
        flex-direction: column;
        min-width: 400px;
        background-color: white;
        border-radius: 8px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      }

      .pb-dialog-header {
        padding: 20px 24px;
        border-bottom: 1px solid #e0e0e0;
        font-size: 18px;
        font-weight: 600;
        color: #333;
      }

      .pb-dialog-body {
        padding: 24px;
        flex: 1;
        color: #666;
        font-size: 14px;
        line-height: 1.6;
      }

      .pb-dialog-footer {
        padding: 16px 24px;
        border-top: 1px solid #e0e0e0;
        display: flex;
        gap: 8px;
        justify-content: flex-end;
      }
    `,
  ],
})
export class PbDialogComponent {
  constructor(public dialogRef: DialogRef<any>) {}
}
