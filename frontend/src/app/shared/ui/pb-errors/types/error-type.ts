export const ErrorTypes = [
  'required',
  'minlength',
  'maxlength',
  'min',
  'pattern',
  'email',
] as const;

export type ErrorType = (typeof ErrorTypes)[number];