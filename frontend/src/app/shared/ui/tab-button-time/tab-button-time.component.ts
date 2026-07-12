import { AsyncPipe, DatePipe } from '@angular/common';
import { Component, input } from '@angular/core';
import { TimeRemainingPipe } from '@features/bet/pipes/time-remaining.pipe';
import { PbIconComponent } from '../icon/pb-icon.component';
import { TabConfig } from '../pb-tabs/tab-config.model';

@Component({
  selector: 'app-tab-button-time',
  imports: [PbIconComponent, DatePipe, AsyncPipe, TimeRemainingPipe],
  template: `
    <span class="tab-button-time">
      <span class="tab-button-time__header">
        <pb-icon icon="timelapse" size="s"></pb-icon>
        <span class="tab-button-time__clock">{{
          tabConfig().label | date: 'HH:mm'
        }}</span>
      </span>
      <span class="tab-button-time__remaining">
        {{ tabConfig().label | timeRemaining: true | async }}
      </span>
    </span>
  `,
  styleUrl: './tab-button-time.component.scss',
})
export class TabButtonTimeComponent {
  tabConfig = input.required<TabConfig<string>>();
}
