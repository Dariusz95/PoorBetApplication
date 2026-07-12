import {
  afterNextRender,
  Component,
  contentChildren,
  effect,
  input,
  signal,
  TemplateRef,
  viewChild,
  ViewEncapsulation,
} from '@angular/core';
import { isNotNil } from '@shared/utils/is-not-nil.util';
import { PbTabContentComponent } from '../pb-tab-content/pb-tab-content.component';
import { PbTabsComponent } from '../pb-tabs/pb-tabs.component';
import { TabConfig } from '../tab-config.model';

@Component({
  selector: 'pb-tab-container',
  standalone: true,
  imports: [PbTabsComponent],
  templateUrl: './pb-tab-container.component.html',
  styleUrl: './pb-tab-container.component.scss',
  encapsulation: ViewEncapsulation.None,
})
export class PbTabContainerComponent<T> {
  tabs = input<TabConfig<T>[]>([]);
  defaultTab = input<T | null>(null);
  selectedTab = signal<T | null>(null);
  isAnimating = signal(false);

  tabContents = contentChildren(PbTabContentComponent, { descendants: true });
  tabsContainer = viewChild<PbTabsComponent<T>>('tabsContainer');

  tabButtonContent = input<TemplateRef<{ $implicit: TabConfig<T> }> | null>(
    null,
  );

  constructor() {
    afterNextRender(() => {
      this.tabs();

      this.setDefaultTab();
    });

    effect(() => {
      this.tabs();

      this.setDefaultTab();
    });
  }

  private setDefaultTab(): void {
    const tabList = this.tabs();
    const initial = this.defaultTab() ?? tabList?.[0]?.value;
    const active = tabList.find((tab) => tab.value === this.selectedTab());

    if (isNotNil(active)) {
      this.setSelected(active.value);
      this.tabsContainer()?.updateIndicatorPosition();

      return;
    }

    if (isNotNil(initial)) {
      this.setSelected(initial as T);
    }
  }

  onTabSelected(value: T): void {
    this.setSelected(value);

    this.isAnimating.set(false);

    requestAnimationFrame(() => {
      this.isAnimating.set(true);
    });

    setTimeout(() => {
      this.isAnimating.set(false);
    }, 300);
  }

  private setSelected(value: T): void {
    this.selectedTab.set(value);
    this.updateContentVisibility();
  }

  private updateContentVisibility(): void {
    const current = this.selectedTab();

    this.tabContents().forEach((content) => {
      content.active.set(content.value === current);
    });
  }
}
