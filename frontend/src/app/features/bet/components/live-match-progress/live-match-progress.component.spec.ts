import { ComponentFixture, TestBed } from '@angular/core/testing';
import { describe, expect, it } from 'vitest';
import { LiveMatchProgressComponent } from './live-match-progress.component';

describe('LiveMatchProgressComponent', () => {
  let fixture: ComponentFixture<LiveMatchProgressComponent>;

  async function createWithMinute(minute: number): Promise<void> {
    await TestBed.configureTestingModule({
      imports: [LiveMatchProgressComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(LiveMatchProgressComponent);
    fixture.componentRef.setInput('minute', minute);
    fixture.detectChanges();
  }

  function barWidthPercent(): string | null {
    const bar: HTMLElement = fixture.nativeElement.querySelector(
      '.live-match-progress__bar',
    );
    return bar.style.width;
  }

  it('should render a proportional width for a minute within the match', async () => {
    await createWithMinute(45);

    expect(barWidthPercent()).toBe('50%');
  });

  it('should clamp the width at 100% for minutes beyond the match length', async () => {
    await createWithMinute(120);

    expect(barWidthPercent()).toBe('100%');
  });
});
