import { ImageType } from '@shared/ui/pb-image/image-type.model';
import { beforeEach, describe, expect, it } from 'vitest';
import { ImagePathPipe } from './image-path.pipe';

describe('ImagePathPipe', () => {
  let pipe: ImagePathPipe;

  beforeEach(() => {
    pipe = new ImagePathPipe();
  });

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  describe('transform method', () => {
    it('should return user icon path for IconType.User', () => {
      const result = pipe.transform(ImageType.User);
      expect(result).toBe('assets/user.svg');
    });

    it('should return Polish flag icon path for IconType.PlFlag', () => {
      const result = pipe.transform(ImageType.PlFlag);
      expect(result).toBe('assets/flags/pl.svg');
    });

    it('should return English flag icon path for IconType.EnFlag', () => {
      const result = pipe.transform(ImageType.EnFlag);
      expect(result).toBe('assets/flags/gb.svg');
    });

    it('should return empty string for unknown icon type', () => {
      const unknownType = 'unknown-icon' as unknown as ImageType;
      const result = pipe.transform(unknownType);
      expect(result).toBe('');
    });

    it('should return consistent paths on multiple calls', () => {
      const result1 = pipe.transform(ImageType.User);
      const result2 = pipe.transform(ImageType.User);
      expect(result1).toBe(result2);
    });
  });
});
