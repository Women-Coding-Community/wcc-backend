import {jwtDecode} from 'jwt-decode';

export type DecodedToken = { exp?: number; [k: string]: any };

const STORAGE_KEY = 'wcc_token';
const EXPIRES_AT_KEY = 'wcc_token_expires_at';

export function getStoredToken(): string | null {
  if (typeof window === 'undefined') return null;
  return localStorage.getItem(STORAGE_KEY);
}

export function getStoredExpiresAt(): string | null {
  if (typeof window === 'undefined') return null;
  return localStorage.getItem(EXPIRES_AT_KEY);
}

export function storeToken(token: string) {
  if (typeof window === 'undefined') return;
  localStorage.setItem(STORAGE_KEY, token);
}

export function storeTokenExpiry(expiresAtIso: string | null | undefined) {
  if (typeof window === 'undefined') return;
  if (expiresAtIso) localStorage.setItem(EXPIRES_AT_KEY, expiresAtIso);
}

export function clearToken() {
  if (typeof window === 'undefined') return;
  localStorage.removeItem(STORAGE_KEY);
  localStorage.removeItem(EXPIRES_AT_KEY);
}

/**
 * Checks if the provided token is expired by first attempting to validate
 * against a stored expiration value. If unavailable or invalid, it falls back
 * to checking the `exp` claim for JWT tokens. Assumes the token is not expired
 * if no valid expiration data can be determined.
 *
 * @param {string} token - The token to be checked for expiration. Could be a JSON Web Token (JWT) or another token format.
 * @return {boolean} `true` if the token is expired, otherwise `false`.
 */
export function isTokenExpired(token: string): boolean {
  try {
    const expiresAtStr = getStoredExpiresAt();
    if (expiresAtStr) {
      const expiresAt = new Date(expiresAtStr).getTime();
      if (!Number.isNaN(expiresAt)) {
        return Date.now() > expiresAt;
      }
    }
  } catch {
    // ignore and fallback
  }

  try {
    const decoded = jwtDecode<DecodedToken>(token);
    if (!decoded.exp) return false;
    const nowSec = Math.floor(Date.now() / 1000);
    return decoded.exp < nowSec;
  } catch {
    return false;
  }
}
