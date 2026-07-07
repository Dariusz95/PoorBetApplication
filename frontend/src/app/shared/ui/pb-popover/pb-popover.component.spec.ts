import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { beforeEach, describe, expect, it } from 'vitest';
import { PbPopoverComponent } from './pb-popover.component';

describe('PbPopoverComponent', () => {
  let component: PbPopoverComponent;
  let fixture: ComponentFixture<PbPopoverComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PbPopoverComponent, NoopAnimationsModule],
    }).compileComponents();

    fixture = TestBed.createComponent(PbPopoverComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be closed by default', () => {
    expect(component.isOpen()).toBe(false);
  });

  it('should default width to 180px', () => {
    expect(component.width()).toBe('180px');
  });

  it('should default to a single end/bottom-anchored position', () => {
    expect(component.positions()).toEqual([
      {
        originX: 'end',
        originY: 'bottom',
        overlayX: 'end',
        overlayY: 'top',
      },
    ]);
  });

  describe('toggle', () => {
    it('should open when closed', () => {
      component.toggle();
      expect(component.isOpen()).toBe(true);
    });

    it('should close when open', () => {
      component.open();
      component.toggle();
      expect(component.isOpen()).toBe(false);
    });
  });

  describe('open', () => {
    it('should set isOpen to true', () => {
      component.open();
      expect(component.isOpen()).toBe(true);
    });

    it('should be idempotent when already open', () => {
      component.open();
      component.open();
      expect(component.isOpen()).toBe(true);
    });
  });

  describe('close', () => {
    it('should set isOpen to false', () => {
      component.open();
      component.close();
      expect(component.isOpen()).toBe(false);
    });

    it('should be idempotent when already closed', () => {
      component.close();
      expect(component.isOpen()).toBe(false);
    });
  });

  it('should toggle the trigger click handler in the template', () => {
    const trigger = fixture.nativeElement.querySelector(
      '.pb-popover__trigger',
    ) as HTMLElement;

    trigger.click();
    fixture.detectChanges();
    expect(component.isOpen()).toBe(true);

    trigger.click();
    fixture.detectChanges();
    expect(component.isOpen()).toBe(false);
  });
});
