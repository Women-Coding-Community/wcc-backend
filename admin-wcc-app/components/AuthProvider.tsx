"use client";
import React, {createContext, useContext, useEffect, useMemo, useState} from 'react';
import {useRouter} from 'next/router';
import {apiFetch} from '@/lib/api';
import {clearToken, getStoredToken, isTokenExpired, storeToken, storeTokenExpiry} from '@/lib/auth';

export type AuthContextType = {
  token: string | null;
  member: object | null;
  roles: string[];
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({children}) => {
  const [member, setMember] = useState<object | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [roles, setRoles] = useState<string[]>([]);
  const router = useRouter();

  useEffect(() => {
    const token = getStoredToken();
    if (token && !isTokenExpired(token)) {
      setToken(token);

      apiFetch<{ roles: string[], member: object }>(`/api/auth/me`, {token: token})
      .then(data => {
        setRoles(data.roles);
        setMember(data.member);
      })
      .catch(() => {
      });
    } else {
      clearToken();
    }
  }, []);

  const login = async (email: string, password: string) => {
    const res = await apiFetch<{
      member: object;
      token: string;
      roles: string[];
      expiresAt?: string
    }>(`/api/auth/login`, {
      method: 'POST', body: {email, password}
    });

    storeToken(res.token);
    storeTokenExpiry(res.expiresAt);

    setToken(res.token);
    setRoles(res.roles);
    setMember(res.member);

    await router.push('/admin');
  };

  const logout = () => {
    clearToken();

    setToken(null);
    setRoles([]);
    setMember(null);

    router.push('/login');
  };

  const value = useMemo(() =>
      ({token, member, roles, login, logout}), [member, token, roles]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
