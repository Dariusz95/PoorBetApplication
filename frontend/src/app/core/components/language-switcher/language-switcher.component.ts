import { Component, computed, inject, viewChild } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { TranslocoService } from '@jsverse/transloco';
import { PbImageComponent } from '@shared/ui/pb-image/pb-image.component';
import { PbPopoverComponent } from '@shared/ui/pb-popover/pb-popover.component';
import { IMAGE_MAP } from './consts/image-map';
import { LanguageContentComponent } from './language-content/language-content.component';

@Component({
  selector: 'app-language-switcher',
  imports: [PbPopoverComponent, PbImageComponent, LanguageContentComponent],
  templateUrl: './language-switcher.component.html',
  styleUrls: ['./language-switcher.component.scss'],
})
export class LanguageSwitcherComponent {
  private readonly translocoService = inject(TranslocoService);
  private readonly popover = viewChild.required(PbPopoverComponent);

  readonly activeLanguage = toSignal(this.translocoService.langChanges$);

  readonly imageType = computed(() => {
    const code = this.activeLanguage();
    return code ? IMAGE_MAP[code] : undefined;
  });

  closePopover(): void {
    this.popover().close();
  }
}
