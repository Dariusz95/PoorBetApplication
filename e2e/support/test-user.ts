export interface TestUser {
  email: string;
  password: string;
}

export function createTestUser(): TestUser {
  const uniqueSuffix = `${Date.now()}-${Math.floor(Math.random() * 1_000_000)}`;

  return {
    email: `e2e-${uniqueSuffix}@poorbet.test`,
    password: "Test1234!",
  };
}

export const SETUP_USER: TestUser = {
  email: "test@test.pl",
  password: "zaq1@WSX",
};
