import { DialogRef } from '@angular/cdk/dialog';
import { CommonModule } from '@angular/common';
import { Component, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { TranslocoDirective } from '@jsverse/transloco';
import { PbButtonComponent } from '@shared/ui/pb-button/pb-button.component';
import { PbCardBodyDirective } from '@shared/ui/pb-card/directives/pb-card-body.directive';
import { PbCardHeaderDirective } from '@shared/ui/pb-card/directives/pb-card-header.directive';
import { PbCardComponent } from '@shared/ui/pb-card/pb-card.component';
import { PbIconComponent } from '@shared/ui/icon/pb-icon.component';
import { filter } from 'rxjs';

interface HelpMarker {
  number: number;
  top: string;
  left: string;
  labelKey: string;
}

@Component({
  selector: 'app-bet-tabs-help',
  standalone: true,
  imports: [
    CommonModule,
    TranslocoDirective,
    PbCardComponent,
    PbCardHeaderDirective,
    PbCardBodyDirective,
    PbButtonComponent,
    PbIconComponent,
  ],
  templateUrl: './bet-tabs-help.component.html',
  styleUrl: './bet-tabs-help.component.scss',
})
export class BetTabsHelpComponent {
  private readonly dialogRef = inject(DialogRef<void>);
  private readonly destroyRef = inject(DestroyRef);

  readonly markers: HelpMarker[] = [
    {
      number: 1,
      top: '29%',
      left: '26%',
      labelKey: 'markers.poolTiming',
    },
    {
      number: 2,
      top: '50%',
      left: '34%',
      labelKey: 'markers.placeBet',
    },
  ];

  constructor() {
    this.dialogRef.backdropClick
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.close());

    this.dialogRef.keydownEvents
      .pipe(
        filter((event) => event.key === 'Escape'),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe(() => this.close());
  }

  close(): void {
    this.dialogRef.close();
  }
}
