import { Pipe, PipeTransform } from '@angular/core';
import { ImageType } from '@shared/ui/pb-image/image-type.model';

@Pipe({
  name: 'imagePath',
})
export class ImagePathPipe implements PipeTransform {
  transform(imageType: ImageType): string {
    return this.IMAGE_MAP[imageType] || '';
  }

  private IMAGE_MAP: Record<ImageType, string> = {
    user: 'assets/user.svg',
    'pl-flag': 'assets/flags/pl.svg',
    'en-flag': 'assets/flags/gb.svg',
    'coin-text': 'assets/biedaCoinText.png',
  };
}
