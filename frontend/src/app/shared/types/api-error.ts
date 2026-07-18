export interface ApiError {
  code: string;
  message: string;
  timestamp: string;
  path?: string;
  validationErrors?: { field: string; message: string }[];
}
