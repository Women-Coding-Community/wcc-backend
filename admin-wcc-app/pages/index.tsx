import {useEffect} from 'react';
import {useRouter} from 'next/router';
import {getStoredToken, isTokenExpired} from '@/lib/auth';

export default function Home() {
  const router = useRouter();

  useEffect(() => {
    const token = getStoredToken();
    if (token && !isTokenExpired(token)) router.replace('/admin');
    else router.replace('/login');
  }, [router]);

  return null;
}
