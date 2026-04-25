import { Component, computed, inject, OnInit } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import {
  LangDefinition,
  TranslocoPipe,
  TranslocoService,
} from '@jsverse/transloco';
import { DropdownOption } from '../pb-dropdown/dropdown-option';
import { PbDropdownComponent } from '../pb-dropdown/pb-dropdown.component';
import { IconType } from '../pb-icon/icon-type.model';
import { PbIconComponent } from '../pb-icon/pb-icon.component';
import { toSignal } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-language-switcher',
  imports: [
    PbDropdownComponent,
    ReactiveFormsModule,
    TranslocoPipe,
    PbIconComponent,
  ],
  templateUrl: './language-switcher.component.html',
  styleUrls: ['./language-switcher.component.scss'],
})
export class LanguageSwitcherComponent implements OnInit {
  private readonly translocoService = inject(TranslocoService);

  languageDropdownOptions: DropdownOption[] = [];
  activeLanguage = toSignal(this.translocoService.langChanges$);

  iconType = computed(() => this.getIconType(this.activeLanguage()));

  ngOnInit(): void {
    this.languageDropdownOptions = this.buildLanguageOptions();
  }

  getIconType(code: string | undefined): IconType | undefined {
    if (!code) {
      return undefined;
    }

    const iconMap: Record<string, IconType> = {
      pl: IconType.PlFlag,
      en: IconType.EnFlag,
    };

    return iconMap[code];
  }

  private buildLanguageOptions(): DropdownOption[] {
    return this.translocoService
      .getAvailableLangs()
      .map((lang) => this.mapToDropdownOption(lang));
  }

  private mapToDropdownOption(lang: string | LangDefinition): DropdownOption {
    const code = typeof lang === 'string' ? lang : lang.label;
    const label = `lang.${code}`;

    return {
      label,
      value: code,
      icon: this.getIconType(code),
      action: () => this.translocoService.setActiveLang(code),
    };
  }
}
