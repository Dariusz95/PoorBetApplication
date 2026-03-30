import { Pipe, PipeTransform } from '@angular/core';
import { IconType } from '../components/pb-icon/pb-icon.model';

@Pipe({
  name: 'iconPath',
})
export class IconPathPipe implements PipeTransform {
  transform(iconType: IconType): string {
    return this.ICON_MAP[iconType] || '';
  }

  private ICON_MAP: Record<IconType, string> = {
    [IconType.User]: 'assets/user.svg',
    [IconType.PlFlag]: 'assets/flags/pl.svg',
    [IconType.EnFlag]: 'assets/flags/gb.svg',
  };
}
