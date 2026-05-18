import { ComponentFixture, TestBed } from '@angular/core/testing';

import { beforeEach, describe, expect, it } from 'vitest';
import { ImageType } from './image-type.model';
import { PbImageComponent } from './pb-image.component';

describe('PbImageComponent', () => {
  let component: PbImageComponent;
  let fixture: ComponentFixture<PbImageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PbImageComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(PbImageComponent);
    component = fixture.componentInstance;

    fixture.componentRef.setInput('type', ImageType.User);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should use md size by default', () => {
    expect(component.size()).toBe('md');
    expect(component.sizeClasses()).toContain('w-5 h-5');
  });

  it('should apply correct size classes for lg', () => {
    fixture.componentRef.setInput('size', 'lg');

    fixture.detectChanges();

    expect(component.sizeClasses()).toContain('w-6 h-6');
  });

  it('should append custom class', () => {
    fixture.componentRef.setInput('customClass', 'text-red-500');

    fixture.detectChanges();

    expect(component.sizeClasses()).toContain('text-red-500');
  });

  it('should trim empty custom class', () => {
    fixture.componentRef.setInput('customClass', undefined);

    fixture.detectChanges();

    expect(component.sizeClasses()).toBe('w-5 h-5');
  });

  it('should update computed classes when size changes', () => {
    fixture.componentRef.setInput('size', 'sm');

    fixture.detectChanges();

    expect(component.sizeClasses()).toBe('w-4 h-4');
  });

  it('should accept ariaLabel input', () => {
    fixture.componentRef.setInput('ariaLabel', 'Home icon');

    fixture.detectChanges();

    expect(component.ariaLabel()).toBe('Home icon');
  });

  it('should accept alt input', () => {
    fixture.componentRef.setInput('alt', 'Some alt');

    fixture.detectChanges();

    expect(component.alt()).toBe('Some alt');
  });
});
