import { ChangeDetectionStrategy, Component } from '@angular/core';
import { PbCardBodyDirective } from '@shared/ui/pb-card/directives/pb-card-body.directive';
import { PbCardHeaderDirective } from '@shared/ui/pb-card/directives/pb-card-header.directive';
import { PbCardComponent } from '@shared/ui/pb-card/pb-card.component';
import { PbSkeletonComponent } from '@shared/ui/pb-skeleton/pb-skeleton.component';

@Component({
  selector: 'app-live-match-skeleton',
  standalone: true,
  imports: [
    PbCardComponent,
    PbCardHeaderDirective,
    PbCardBodyDirective,
    PbSkeletonComponent,
  ],
  templateUrl: './live-match-skeleton.component.html',
  styleUrl: './live-match-skeleton.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LiveMatchSkeletonComponent {}
