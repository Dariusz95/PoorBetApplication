import { ComponentFixture, TestBed } from '@angular/core/testing';
import { getTranslocoModule } from '@shared/utils/get-transloco-module';
import { of } from 'rxjs';
import { beforeEach, describe, expect, it } from 'vitest';
import { AuthService } from '../../auth/services/auth.service';
import { HeaderComponent } from './header.component';

describe('HeaderComponent', () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HeaderComponent, getTranslocoModule()],
      providers: [
        { provide: AuthService, useValue: { isLoggedIn$: of(true) } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
