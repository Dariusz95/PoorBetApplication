export const ImageSize = ['xs', 'sm', 'md', 'lg'] as const;
export type ImageSize = (typeof ImageSize)[number];
