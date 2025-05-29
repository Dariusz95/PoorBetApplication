export const ErrorTypes = [
  'required',
  'minlength',
  'maxlength',
  'pattern',
  'email',
] as const;

export type ErrorType = (typeof ErrorTypes)[number];