const ERROR_CODE_TRANSLATION_KEYS: Record<string, string> = {
  INVALID_CREDENTIALS: 'auth.login.invalidCredentials',
  INSUFFICIENT_FUNDS: 'errors.insufficientFunds',
  WALLET_NOT_FOUND: 'errors.walletNotFound',
  WALLET_UNAVAILABLE: 'errors.walletUnavailable',
  RESOURCE_ALREADY_EXISTS: 'errors.resourceAlreadyExists',
  RESOURCE_NOT_FOUND: 'errors.resourceNotFound',
  VALIDATION_ERROR: 'errors.validation',
  MALFORMED_REQUEST: 'errors.malformedRequest',
  UNSUPPORTED_MEDIA_TYPE: 'errors.unsupportedMediaType',
};

export const apiErrorTranslationKey = (code: string | undefined): string =>
  (code && ERROR_CODE_TRANSLATION_KEYS[code]) || 'errors.generic';
