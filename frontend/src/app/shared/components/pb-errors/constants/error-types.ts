export type MinLengthError = {
  requiredLength: number;
  actualLength: number;
};

export type MaxLengthError = {
  requiredLength: number;
  actualLength: number;
};

export type PatternError = {
  requiredPattern: string;
  actualValue?: string;
};

export type ErrorValueMapBase = {
  required: boolean;
  minlength: MinLengthError;
  maxlength: MaxLengthError;
  pattern: PatternError;
  email: boolean;
};
