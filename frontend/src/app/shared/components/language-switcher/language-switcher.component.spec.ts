import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LanguageSwitcherComponent } from './language-switcher.component';
import { beforeEach, describe, expect, it } from 'vitest';
import { getTranslocoModule } from '@shared/utils/get-transloco-module';

describe('LanguageSwitcherComponent', () => {
  let component: LanguageSwitcherComponent;
  let fixture: ComponentFixture<LanguageSwitcherComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LanguageSwitcherComponent, getTranslocoModule()]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LanguageSwitcherComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
