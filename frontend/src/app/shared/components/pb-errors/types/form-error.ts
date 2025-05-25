import { FormErrorParam } from './form-error-param';

export type FormError = {
  translateKey: string;
  params?: FormErrorParam;
};
