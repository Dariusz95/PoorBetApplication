import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { TranslocoService } from '@jsverse/transloco'; // Importuj TranslocoService
import { PbDropdownComponent } from '../pb-dropdown/pb-dropdown.component';

@Component({
  selector: 'app-language-switcher',
  standalone: true,
  imports: [CommonModule, PbDropdownComponent],
  templateUrl: './language-switcher.component.html',
  styleUrls: ['./language-switcher.component.scss'],
})
export class LanguageSwitcherComponent implements OnInit {
  private readonly translocoService = inject(TranslocoService);
  availableLanguages: { code: string; label: string }[] = [];
  activeLanguage!: string;

  ngOnInit(): void {
    this.availableLanguages = this.translocoService
      .getAvailableLangs()
      .map((lang) => {
        const code = typeof lang === 'string' ? lang : lang.label;
        const label = code.toUpperCase();
        return { code, label };
      });

    this.translocoService.langChanges$.subscribe((lang) => {
      this.activeLanguage = lang;
    });

    this.activeLanguage = this.translocoService.getActiveLang();
  }

  changeLanguage(selectedOption: { code: string; label: string }): void {
    if (this.activeLanguage !== selectedOption.code) {
      this.translocoService.setActiveLang(selectedOption.code);
    }
  }

  getActiveLanguageLabel(): string {
    const activeLangObj = this.availableLanguages.find(
      (lang) => lang.code === this.activeLanguage
    );
    return activeLangObj
      ? activeLangObj.label
      : this.activeLanguage.toUpperCase();
  }
}
