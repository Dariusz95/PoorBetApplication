import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import {
  LangDefinition,
  TranslocoPipe,
  TranslocoService,
} from '@jsverse/transloco'; // Importuj TranslocoService
import { DropdownOption } from '../pb-dropdown/dropdown-option';
import { PbDropdownComponent } from '../pb-dropdown/pb-dropdown.component';

@Component({
  selector: 'app-language-switcher',
  imports: [
    CommonModule,
    PbDropdownComponent,
    ReactiveFormsModule,
    TranslocoPipe,
  ],
  templateUrl: './language-switcher.component.html',
  styleUrls: ['./language-switcher.component.scss'],
})
export class LanguageSwitcherComponent implements OnInit {
  private readonly translocoService = inject(TranslocoService);

  availableLanguages: DropdownOption[] = [];
  activeLanguageControl = new FormControl<string>('');

  ngOnInit(): void {
    this.availableLanguages = this.translocoService
      .getAvailableLangs()
      .map((lang) => this.mapToDropdownOption(lang));

    this.translocoService.langChanges$.subscribe((lang) => {
      this.activeLanguageControl.setValue(lang, { emitEvent: false });
    });

    this.activeLanguageControl.valueChanges.subscribe((lang) => {
      this.translocoService.setActiveLang(lang!);
    });
  }

  private mapToDropdownOption(lang: string | LangDefinition): DropdownOption {
    const code = typeof lang === 'string' ? lang : lang.label;
    const label = `lang.${code}`;

    return { value: code, label };
  }
}
