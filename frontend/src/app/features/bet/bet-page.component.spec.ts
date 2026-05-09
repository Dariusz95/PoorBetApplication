import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BetPageComponent } from './bet-page.component';
import { beforeEach, describe, expect, it } from 'vitest';
import { getTranslocoModule } from '@shared/utils/get-transloco-module';

describe('BetPageComponent', () => {
  let component: BetPageComponent;
  let fixture: ComponentFixture<BetPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BetPageComponent, getTranslocoModule()]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BetPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
