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
    [ImageType.User]: 'assets/user.svg',
    [ImageType.PlFlag]: 'assets/flags/pl.svg',
    [ImageType.EnFlag]: 'assets/flags/gb.svg',
  };
}
