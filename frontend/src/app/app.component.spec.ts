import { TestBed } from '@angular/core/testing';
import { getTranslocoModule } from '@shared/utils/get-transloco-module';
import { AppComponent } from './app.component';
import { beforeEach, describe, expect, it } from 'vitest';

describe('AppComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppComponent, getTranslocoModule()],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
