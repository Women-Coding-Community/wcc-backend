import { isTokenExpired } from '@/lib/auth';

describe('isTokenExpired', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('returns false when stored expiresAt is in the future', () => {
    const futureIso = new Date(Date.now() + 3600_000).toISOString();
    localStorage.setItem('wcc_token_expires_at', futureIso);
    expect(isTokenExpired('opaque-token')).toBe(false);
  });

  it('returns true when stored expiresAt is in the past', () => {
    const pastIso = new Date(Date.now() - 3600_000).toISOString();
    localStorage.setItem('wcc_token_expires_at', pastIso);
    expect(isTokenExpired('opaque-token')).toBe(true);
  });

  it('treats non-JWT tokens as not expired when no stored expiry', () => {
    expect(isTokenExpired('not-a-jwt')).toBe(false);
  });
});
