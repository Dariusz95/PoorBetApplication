function requireEnv(name: string): string {
  const value = process.env[name];

  if (!value) {
    throw new Error(
      `Brakuje zmiennej środowiskowej "${name}". Sprawdź plik .env.e2e.local (wzorzec: .env.e2e.example).`,
    );
  }

  return value;
}

export const e2eEnv = {
  get baseUrl(): string {
    return process.env['E2E_BASE_URL'] ?? 'http://localhost:4200';
  },
  get apiBaseUrl(): string {
    return requireEnv('E2E_API_BASE_URL');
  },
};
