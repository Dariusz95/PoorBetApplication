import {
  Component,
  computed,
  ElementRef,
  inject,
  output,
  viewChildren,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import {
  LangDefinition,
  TranslocoPipe,
  TranslocoService,
} from '@jsverse/transloco';
import { ImageType } from '@shared/ui/pb-image/image-type.model';
import { PbImageComponent } from '@shared/ui/pb-image/pb-image.component';
import { IMAGE_MAP } from '../consts/image-map';

interface LanguageOption {
  code: string;
  labelKey: string;
  imageType: ImageType | undefined;
}

@Component({
  selector: 'app-language-content',
  imports: [PbImageComponent, TranslocoPipe],
  templateUrl: './language-content.component.html',
  styleUrls: ['./language-content.component.scss'],
})
export class LanguageContentComponent {
  private readonly translocoService = inject(TranslocoService);

  readonly langSelected = output<void>();

  readonly activeLang = toSignal(this.translocoService.langChanges$);

  readonly languages = computed<LanguageOption[]>(() =>
    this.translocoService.getAvailableLangs().map((lang) => {
      const code =
        typeof lang === 'string' ? lang : (lang as LangDefinition).id;
      return {
        code,
        labelKey: `lang.${code}`,
        imageType: IMAGE_MAP[code],
      };
    }),
  );

  private readonly itemRefs =
    viewChildren<ElementRef<HTMLButtonElement>>('menuItem');

  setLang(code: string): void {
    this.translocoService.setActiveLang(code);
    this.langSelected.emit();
  }

  onKeydown(event: KeyboardEvent, index: number): void {
    const items = this.itemRefs();
    if (!items.length) {
      return;
    }

    let nextIndex: number;
    switch (event.key) {
      case 'ArrowDown':
        nextIndex = (index + 1) % items.length;
        break;
      case 'ArrowUp':
        nextIndex = (index - 1 + items.length) % items.length;
        break;
      case 'Home':
        nextIndex = 0;
        break;
      case 'End':
        nextIndex = items.length - 1;
        break;
      default:
        return;
    }

    event.preventDefault();
    items[nextIndex].nativeElement.focus();
  }
}
