import { NgTemplateOutlet } from '@angular/common';
import {
  afterRenderEffect,
  Component,
  ElementRef,
  input,
  output,
  signal,
  TemplateRef,
  viewChild,
} from '@angular/core';
import { TabConfig } from '../tab-config.model';

@Component({
  selector: 'pb-tabs',
  standalone: true,
  imports: [NgTemplateOutlet],
  templateUrl: './pb-tabs.component.html',
  styleUrl: './pb-tabs.component.scss',
})
export class PbTabsComponent<T> {
  tabs = input<TabConfig<T>[]>([]);
  selectedTab = input<T | null>(null);
  tabButtonContent = input<TemplateRef<{ $implicit: TabConfig<T> }> | null>(
    null,
  );

  selectedTabChange = output<T>();

  tabsContainer = viewChild<ElementRef>('tabsContainer');

  indicatorStyle = signal({ left: '0px', width: '0px' });

  constructor() {
    afterRenderEffect(() => {
      this.selectedTab();

      this.updateIndicatorPosition();
    });
  }

  selectTab(tab: TabConfig<T>): void {
    this.selectedTabChange.emit(tab.value);
  }

  updateIndicatorPosition(): void {
    if (!this.tabsContainer()) {
      return;
    }

    const activeButton = this.tabsContainer()!.nativeElement.querySelector(
      '.tab-button.active',
    ) as HTMLElement | null;

    if (!activeButton) {
      return;
    }

    const left = activeButton.offsetLeft;
    const width = activeButton.offsetWidth;

    this.indicatorStyle.set({ left: `${left}px`, width: `${width}px` });
  }
}
