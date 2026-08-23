import { describe, expect, it } from 'vitest';
import { getTeamAvatarColorClass, getTeamInitials } from './team-avatar.util';

describe('getTeamInitials', () => {
  it('should use the first letter of the first two words for multi-word names', () => {
    expect(getTeamInitials('Real Madrid')).toBe('RM');
  });

  it('should use the first two letters for a single-word name', () => {
    expect(getTeamInitials('Barcelona')).toBe('BA');
  });

  it('should ignore extra whitespace between words', () => {
    expect(getTeamInitials('  Real   Madrid  ')).toBe('RM');
  });

  it('should return an empty string for an empty name', () => {
    expect(getTeamInitials('')).toBe('');
  });
});

describe('getTeamAvatarColorClass', () => {
  it('should deterministically return the same class for the same id', () => {
    const id = 'team-123';
    expect(getTeamAvatarColorClass(id)).toBe(getTeamAvatarColorClass(id));
  });

  it('should return one of the known avatar color classes', () => {
    expect(getTeamAvatarColorClass('team-abc')).toMatch(/^bg-app-/);
  });
});
