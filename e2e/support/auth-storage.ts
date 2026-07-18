import path from 'path';

const AUTH_DIR = path.resolve(__dirname, '../.auth');

export function authFile(role: string): string {
  return path.join(AUTH_DIR, `${role}.json`);
}
