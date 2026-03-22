export const ButtonVariant = ['primary', 'secondary', 'outline', 'link'] as const;
export type ButtonVariant = typeof ButtonVariant[number];

export const ButtonSize = ['sm', 'md', 'lg', 'responsive'] as const;
export type ButtonSize = typeof ButtonSize[number];
