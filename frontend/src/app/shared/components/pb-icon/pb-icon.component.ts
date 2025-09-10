import { CommonModule } from '@angular/common';
import { Component, computed, input } from '@angular/core';
import { IconPathPipe } from '../../pipes/icon-path.pipe';
import { IconSize, IconType } from './pb-icon.model';

@Component({
  selector: 'pb-icon',
  imports: [IconPathPipe, CommonModule],
  templateUrl: './pb-icon.component.html',
  styleUrl: './pb-icon.component.scss',
})
export class PbIconComponent {
  type = input.required<IconType>();
  size = input<IconSize>('md');
  alt = input<string | undefined>();
  ariaLabel = input<string | undefined>();
  customClass = input<string | undefined>();

  IconType = IconType;
  IconSize = IconSize;

  sizeClasses = computed(() => {
    const sizeMap: Record<IconSize, string> = {
      xs: 'w-3 h-3',
      sm: 'w-4 h-4',
      md: 'w-5 h-5',
      lg: 'w-6 h-6',
    };

    return `${sizeMap[this.size()]} ${this.customClass() ?? ''}`.trim();
  });
}
