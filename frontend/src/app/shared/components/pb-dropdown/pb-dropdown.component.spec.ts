import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DropdownOption } from './dropdown-option';
import { PbDropdownComponent } from './pb-dropdown.component';

describe('PbDropdownComponent', () => {
  let component: PbDropdownComponent<DropdownOption>;
  let fixture: ComponentFixture<PbDropdownComponent<DropdownOption>>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PbDropdownComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(PbDropdownComponent<DropdownOption>);
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

  it('should toggle dropdown state', () => {
    expect(component.isOpen()).toBeFalse();

    component.toggleDropdown();
    expect(component.isOpen()).toBeTrue();

    component.closeDropdown();
    expect(component.isOpen()).toBeFalse();
  });
});
