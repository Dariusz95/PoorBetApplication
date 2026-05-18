import { Component, computed, inject, OnInit } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import {
  LangDefinition,
  TranslocoPipe,
  TranslocoService,
} from '@jsverse/transloco';
import { DropdownOption } from '../../../shared/ui/pb-dropdown/dropdown-option';
import { PbDropdownComponent } from '../../../shared/ui/pb-dropdown/pb-dropdown.component';
import { ImageType } from '../../../shared/ui/pb-image/image-type.model';
import { PbImageComponent } from '@shared/ui/pb-image/pb-image.component';

@Component({
  selector: 'app-language-switcher',
  imports: [
    PbDropdownComponent,
    ReactiveFormsModule,
    TranslocoPipe,
    PbImageComponent,
  ],
  templateUrl: './language-switcher.component.html',
  styleUrls: ['./language-switcher.component.scss'],
})
export class LanguageSwitcherComponent implements OnInit {
  private readonly translocoService = inject(TranslocoService);

  languageDropdownOptions: DropdownOption[] = [];
  activeLanguage = toSignal(this.translocoService.langChanges$);

  imageType = computed(() => this.getImageType(this.activeLanguage()));

  ngOnInit(): void {
    this.languageDropdownOptions = this.buildLanguageOptions();
  }

  getImageType(code: string | undefined): ImageType | undefined {
    if (!code) {
      return undefined;
    }

    const imageMap: Record<string, ImageType> = {
      pl: ImageType.PlFlag,
      en: ImageType.EnFlag,
    };

    return imageMap[code];
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
      icon: this.getImageType(code),
      action: () => this.translocoService.setActiveLang(code),
    };
  }
}
