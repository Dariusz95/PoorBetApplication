import { Uuid } from '../types/uuid.type';

export function isUuid(value: string): boolean {
  const uuidRegex =
    /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;

  return uuidRegex.test(value);
}

export function toUuid(value: string): Uuid {
  if (!isUuid(value)) {
    throw new Error(`Invalid UUID: ${value}`);
  }
  return value as Uuid;
}
