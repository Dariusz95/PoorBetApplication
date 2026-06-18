import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { PbPopoverComponent } from './pb-popover.component';

describe('PbPopoverComponent', () => {
  let component: PbPopoverComponent;
  let fixture: ComponentFixture<PbPopoverComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PbPopoverComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(PbPopoverComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('options', [
      { value: 'pl', label: 'lang.pl' },
      { value: 'en', label: 'lang.en' },
    ]);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Dropdown Toggle', () => {
    it('should toggle dropdown state', () => {
      expect(component.isOpen()).toBe(false);

      component.toggleDropdown();
      expect(component.isOpen()).toBe(true);

      component.toggleDropdown();
      expect(component.isOpen()).toBe(false);
    });

    it('should close dropdown', () => {
      component.isOpen.set(true);
      component.closeDropdown();
      expect(component.isOpen()).toBe(false);
    });

    it('should call onTouched when closing dropdown', () => {
      const onTouchedSpy = vi.fn();
      component.registerOnTouched(onTouchedSpy);

      component.closeDropdown();
      expect(onTouchedSpy).toHaveBeenCalled();
    });

    it('should toggle multiple times', () => {
      const toggleStates = [];

      for (let i = 0; i < 4; i++) {
        component.toggleDropdown();
        toggleStates.push(component.isOpen());
      }

      expect(toggleStates).toEqual([true, false, true, false]);
    });
  });

  describe('Option Selection', () => {
    it('should select option and update value', () => {
      const option: DropdownOption = { value: 'pl', label: 'lang.pl' };
      const onChangeSpy = vi.fn();
      component.registerOnChange(onChangeSpy);

      component.selectOption(option);

      expect(component.value()).toBe('pl');
      expect(onChangeSpy).toHaveBeenCalledWith('pl');
    });

    it('should close dropdown after selecting option when closeOnSelect is true', () => {
      fixture.componentRef.setInput('closeOnSelect', true);
      component.isOpen.set(true);

      const option: DropdownOption = { value: 'en', label: 'lang.en' };
      component.selectOption(option);

      expect(component.isOpen()).toBe(false);
    });

    it('should keep dropdown open after selecting option when closeOnSelect is false', () => {
      fixture.componentRef.setInput('closeOnSelect', false);
      component.isOpen.set(true);

      const option: DropdownOption = { value: 'en', label: 'lang.en' };
      component.selectOption(option);

      expect(component.isOpen()).toBe(true);
    });

    it('should execute option action if provided', () => {
      const actionSpy = vi.fn();
      const option: DropdownOption = {
        value: 'pl',
        label: 'lang.pl',
        action: actionSpy,
      };

      component.selectOption(option);
      expect(actionSpy).toHaveBeenCalled();
    });

    it('should execute action before updating value', () => {
      const callOrder: string[] = [];
      const actionSpy = vi.fn().mockImplementation(() => {
        callOrder.push('action');
      });
      const onChangeSpy = vi.fn().mockImplementation(() => {
        callOrder.push('onChange');
      });

      component.registerOnChange(onChangeSpy);

      const option: DropdownOption = {
        value: 'pl',
        label: 'lang.pl',
        action: actionSpy,
      };

      component.selectOption(option);

      expect(callOrder[0]).toBe('action');
      expect(callOrder[1]).toBe('onChange');
    });

    it('should not update value if option has no value', () => {
      const onChangeSpy = vi.fn();
      component.registerOnChange(onChangeSpy);

      const option: DropdownOption = { label: 'No Value' };
      component.selectOption(option);

      expect(component.value()).toBeNull();
      expect(onChangeSpy).not.toHaveBeenCalled();
    });
  });

  describe('ControlValueAccessor', () => {
    it('should write value', () => {
      component.writeValue('pl');
      expect(component.value()).toBe('pl');
    });

    it('should write null value', () => {
      component.writeValue(null);
      expect(component.value()).toBeNull();
    });

    it('should register onChange callback', () => {
      const callbackSpy = vi.fn();
      component.registerOnChange(callbackSpy);

      const option: DropdownOption = { value: 'en', label: 'lang.en' };
      component.selectOption(option);

      expect(callbackSpy).toHaveBeenCalledWith('en');
    });

    it('should register onTouched callback', () => {
      const callbackSpy = vi.fn();
      component.registerOnTouched(callbackSpy);

      component.closeDropdown();
      expect(callbackSpy).toHaveBeenCalled();
    });
  });

  describe('Selected Option', () => {
    it('should return selected option', () => {
      component.writeValue('pl');
      const selected = component.selectedOption();

      expect(selected).toEqual({ value: 'pl', label: 'lang.pl' });
    });

    it('should return undefined when no value is set', () => {
      component.writeValue(null);
      const selected = component.selectedOption();

      expect(selected).toBeUndefined();
    });

    it('should return undefined when value does not match any option', () => {
      component.writeValue('unknown');
      const selected = component.selectedOption();

      expect(selected).toBeUndefined();
    });

    it('should find correct option among multiple options', () => {
      fixture.componentRef.setInput('options', [
        { value: 'pl', label: 'lang.pl' },
        { value: 'en', label: 'lang.en' },
        { value: 'de', label: 'lang.de' },
        { value: 'fr', label: 'lang.fr' },
      ]);

      component.writeValue('de');
      const selected = component.selectedOption();

      expect(selected?.value).toBe('de');
      expect(selected?.label).toBe('lang.de');
    });
  });

  describe('Options Input', () => {
    it('should initialize with provided options', () => {
      expect(component.options().length).toBe(2);
      expect(component.options()[0].value).toBe('pl');
      expect(component.options()[1].value).toBe('en');
    });

    it('should update options dynamically', () => {
      fixture.componentRef.setInput('options', [
        { value: 'it', label: 'lang.it' },
        { value: 'es', label: 'lang.es' },
      ]);

      expect(component.options().length).toBe(2);
      expect(component.options()[0].value).toBe('it');
    });

    it('should handle empty options', () => {
      fixture.componentRef.setInput('options', []);

      const selected = component.selectedOption();
      expect(selected).toBeUndefined();
    });
  });

  describe('Close On Select Config', () => {
    it('should have closeOnSelect true by default', () => {
      expect(component.closeOnSelect()).toBe(true);
    });

    it('should respect closeOnSelect configuration', () => {
      fixture.componentRef.setInput('closeOnSelect', false);
      expect(component.closeOnSelect()).toBe(false);
    });
  });

  describe('Highlighted Option Config', () => {
    it('should have highlightedOption true by default', () => {
      expect(component.highlightedOption()).toBe(true);
    });

    it('should respect highlightedOption configuration', () => {
      fixture.componentRef.setInput('highlightedOption', false);
      expect(component.highlightedOption()).toBe(false);
    });
  });
});
