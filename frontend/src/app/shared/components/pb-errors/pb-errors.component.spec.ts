import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PbErrorsComponent } from './pb-errors.component';
import { beforeEach, describe, expect, it } from 'vitest';
import { IconType } from '../pb-icon/icon-type.model';

describe('PbErrorsComponent', () => {
  let component: PbErrorsComponent;
  let fixture: ComponentFixture<PbErrorsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PbErrorsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PbErrorsComponent);
  
    component = fixture.componentInstance;
    fixture.componentRef.setInput('errors', null);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
