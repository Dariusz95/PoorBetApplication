import { ErrorValueMapBase } from "../constants/error-types";
import { ErrorType } from "./error-type";

export type ErrorValueMap = {
  [K in ErrorType]: ErrorValueMapBase[K];
};

export type ErrorValue<T extends ErrorType> = ErrorValueMap[T];