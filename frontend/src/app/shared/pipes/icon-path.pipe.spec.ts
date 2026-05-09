import { describe, it, beforeEach, expect } from 'vitest';
import { IconType } from '@shared/components/pb-icon/icon-type.model';
import { IconPathPipe } from './icon-path.pipe';

describe('IconPathPipe', () => {
  let pipe: IconPathPipe;

  beforeEach(() => {
    pipe = new IconPathPipe();
  });

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  describe('transform method', () => {
    it('should return user icon path for IconType.User', () => {
      const result = pipe.transform(IconType.User);
      expect(result).toBe('assets/user.svg');
    });

    it('should return Polish flag icon path for IconType.PlFlag', () => {
      const result = pipe.transform(IconType.PlFlag);
      expect(result).toBe('assets/flags/pl.svg');
    });

    it('should return English flag icon path for IconType.EnFlag', () => {
      const result = pipe.transform(IconType.EnFlag);
      expect(result).toBe('assets/flags/gb.svg');
    });

    it('should return empty string for unknown icon type', () => {
      const unknownType = 'unknown-icon' as unknown as IconType;
      const result = pipe.transform(unknownType);
      expect(result).toBe('');
    });

    it('should return consistent paths on multiple calls', () => {
      const result1 = pipe.transform(IconType.User);
      const result2 = pipe.transform(IconType.User);
      expect(result1).toBe(result2);
    });
  });
});
