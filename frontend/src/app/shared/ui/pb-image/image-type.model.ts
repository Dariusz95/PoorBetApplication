export const IMAGE_TYPES = [
  'user',
  'pl-flag',
  'en-flag',
  'coin-text',
] as const;

export type ImageType = (typeof IMAGE_TYPES)[number];
