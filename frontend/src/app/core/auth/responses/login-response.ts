export interface LoginResponse {
  token: string;
  tokenType: string;
  username: string;
  roles: string[];
  expiresAt: number;
}
