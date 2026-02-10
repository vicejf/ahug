import { v4 as uuidv4 } from 'uuid';

export function generateUUID(): string {
  return uuidv4();
}

export function generateShortUUID(): string {
  return uuidv4().replace(/-/g, '').substring(0, 32);
}
