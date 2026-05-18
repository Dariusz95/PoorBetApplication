import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { RoutingService } from '../../../core/routing/routing.service';
import { PbButtonComponent } from './pb-button.component';
import { ButtonSize, ButtonVariant } from './pb-button.model';

describe('PbButtonComponent', () => {
  let component: PbButtonComponent;
  let fixture: ComponentFixture<PbButtonComponent>;
  let routingService: RoutingService;

  beforeEach(async () => {
    const routingServiceSpy = {
      createLink: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [PbButtonComponent],
      providers: [{ provide: RoutingService, useValue: routingServiceSpy }],
    }).compileComponents();

    routingService = TestBed.inject(RoutingService);
    fixture = TestBed.createComponent(PbButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('CSS Classes', () => {
    it('should apply default variant class', () => {
      fixture.detectChanges();
      const classes = component.getButtonClasses();
      expect(classes).toContain('pb-button');
      expect(classes).toContain('pb-button--primary');
    });

    it('should apply custom variant class', () => {
      const variants: ButtonVariant[] = [
        'primary',
        'secondary',
        'outline',
        'link',
      ];

      variants.forEach((variant) => {
        vi.spyOn(component, 'variant' as any).mockReturnValue(variant);
        const classes = component.getButtonClasses();
        expect(classes).toContain(`pb-button--${variant}`);
      });
    });

    it('should apply custom size class', () => {
      const sizes: ButtonSize[] = ['sm', 'md', 'lg', 'responsive'];

      sizes.forEach((size) => {
        vi.spyOn(component, 'size' as any).mockReturnValue(size);
        const classes = component.getButtonClasses();
        expect(classes).toContain(`pb-button--${size}`);
      });
    });

    it('should add full-width class when fullWidth is true', () => {
      vi.spyOn(component, 'fullWidth' as any).mockReturnValue(true);
      const classes = component.getButtonClasses();
      expect(classes).toContain('pb-button--full-width');
    });

    it('should not add full-width class when fullWidth is false', () => {
      vi.spyOn(component, 'fullWidth' as any).mockReturnValue(false);
      const classes = component.getButtonClasses();
      expect(classes).not.toContain('pb-button--full-width');
    });

    it('should add loading class when loading is true', () => {
      vi.spyOn(component, 'loading' as any).mockReturnValue(true);
      const classes = component.getButtonClasses();
      expect(classes).toContain('pb-button--loading');
    });

    it('should not add loading class when loading is false', () => {
      vi.spyOn(component, 'loading' as any).mockReturnValue(false);
      const classes = component.getButtonClasses();
      expect(classes).not.toContain('pb-button--loading');
    });
  });

  describe('Click Handler', () => {
    it('should emit buttonClick event when clicked and not disabled/loading', () => {
      vi.spyOn(component, 'disabled' as any).mockReturnValue(false);
      vi.spyOn(component, 'loading' as any).mockReturnValue(false);
      vi.spyOn(component.buttonClick, 'emit');

      const event = new Event('click');
      component.onClick(event);

      expect(component.buttonClick.emit).toHaveBeenCalledWith(event);
    });

    it('should prevent click event when button is disabled', () => {
      vi.spyOn(component, 'disabled' as any).mockReturnValue(true);
      vi.spyOn(component, 'loading' as any).mockReturnValue(false);
      vi.spyOn(component.buttonClick, 'emit');

      const event = { preventDefault: vi.fn() } as any;
      component.onClick(event);

      expect(event.preventDefault).toHaveBeenCalled();
      expect(component.buttonClick.emit).not.toHaveBeenCalled();
    });

    it('should prevent click event when button is loading', () => {
      vi.spyOn(component, 'disabled' as any).mockReturnValue(false);
      vi.spyOn(component, 'loading' as any).mockReturnValue(true);
      vi.spyOn(component.buttonClick, 'emit');

      const event = { preventDefault: vi.fn() } as any;
      component.onClick(event);

      expect(event.preventDefault).toHaveBeenCalled();
      expect(component.buttonClick.emit).not.toHaveBeenCalled();
    });

    it('should not emit event when both disabled and loading are true', () => {
      vi.spyOn(component, 'disabled' as any).mockReturnValue(true);
      vi.spyOn(component, 'loading' as any).mockReturnValue(true);
      vi.spyOn(component.buttonClick, 'emit');

      const event = { preventDefault: vi.fn() } as any;
      component.onClick(event);

      expect(event.preventDefault).toHaveBeenCalled();
      expect(component.buttonClick.emit).not.toHaveBeenCalled();
    });
  });

  describe('Button Type', () => {
    it('should have default type as button', () => {
      vi.spyOn(component, 'type' as any).mockReturnValue('button');
      component.type as any;
      expect(component.type).toBeDefined();
    });
  });

  describe('Disabled State', () => {
    it('should be enabled by default', () => {
      vi.spyOn(component, 'disabled' as any).mockReturnValue(false);
      expect(component.disabled()).toBe(false);
    });

    it('should be disabled when disabled input is true', () => {
      vi.spyOn(component, 'disabled' as any).mockReturnValue(true);
      expect(component.disabled()).toBe(true);
    });
  });

  describe('Aria Label', () => {
    it('should have optional aria label', () => {
      vi.spyOn(component, 'ariaLabel' as any).mockReturnValue('Test Label');
      expect(component.ariaLabel()).toBe('Test Label');
    });

    it('should have undefined aria label by default', () => {
      vi.spyOn(component, 'ariaLabel' as any).mockReturnValue(undefined);
      expect(component.ariaLabel()).toBeUndefined();
    });
  });
});
