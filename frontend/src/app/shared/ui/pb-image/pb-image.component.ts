import { CommonModule } from '@angular/common';
import { Component, computed, input } from '@angular/core';
import { ImagePathPipe } from '@shared/pipes/image-path.pipe';
import { ImageSize } from './image-size.model';
import { ImageType } from './image-type.model';

@Component({
  selector: 'pb-image',
  imports: [ImagePathPipe, CommonModule],
  templateUrl: './pb-image.component.html',
  styleUrl: './pb-image.component.scss',
})
export class PbImageComponent {
  type = input.required<ImageType>();
  size = input<ImageSize>('md');
  alt = input<string | undefined>();
  ariaLabel = input<string | undefined>();
  customClass = input<string | undefined>();

  ImageSize = ImageSize;

  sizeClasses = computed(() => {
    const sizeMap: Record<ImageSize, string> = {
      xs: 'w-3 h-3',
      sm: 'w-4 h-4',
      md: 'w-5 h-5',
      lg: 'w-6 h-6',
    };

    return `${sizeMap[this.size()]} ${this.customClass() ?? ''}`.trim();
  });
}
