import {
  ChangeDetectionStrategy,
  Component,
  computed,
  input,
} from '@angular/core';

const MATCH_TOTAL_MINUTES = 90;

@Component({
  selector: 'app-live-match-progress',
  template: `
    <div class="h-1 w-10 overflow-hidden rounded-full bg-app-surfaceSoft">
      <div
        class="live-match-progress__bar h-full rounded-full bg-gradient-to-r from-app-primary to-app-accent transition-[width] duration-slow"
        [style.width.%]="progressPercent()"
      ></div>
    </div>
  `,
  styleUrl: './live-match-progress.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LiveMatchProgressComponent {
  readonly minute = input.required<number>();

  protected readonly progressPercent = computed(() =>
    Math.min((this.minute() / MATCH_TOTAL_MINUTES) * 100, 100),
  );
}
