import { useEffect, useState } from 'react';
import { Alert, Box, Button, Paper, Snackbar, Stack, TextField, Typography } from '@mui/material';
import AdminLayout from '@/components/AdminLayout';
import { apiFetch } from '@/lib/api';
import { getStoredToken, isTokenExpired } from '@/lib/auth';
import { useRouter } from 'next/router';

interface UserDto {
  id?: string;
  email: string;
  roles?: string;
}

const USERS_PATH = '/api/platform/v1/users';
const RESET_PATH = '/api/auth/reset-password/request';

export default function UsersPage() {
  const router = useRouter();
  const [items, setItems] = useState<UserDto[]>([]);
  const [email, setEmail] = useState('');
  const [roles, setRoles] = useState('USER');
  const [error, setError] = useState<string | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [resetSuccess, setResetSuccess] = useState<string | null>(null);
  const [resettingEmail, setResettingEmail] = useState<string | null>(null);

  useEffect(() => {
    const stored = getStoredToken();
    if (!stored || isTokenExpired(stored)) router.replace('/login');
    else {
      setToken(stored);
      loadUsers(stored);
    }
  }, [router]);

  const loadUsers = async (t: string) => {
    try {
      const data = await apiFetch<UserDto[]>(USERS_PATH, { token: t });
      setItems(data || []);
    } catch (e: any) {
      setError(e.message);
    }
  };

  const createUser = async () => {
    if (!token) return;
    setError(null);
    try {
      await apiFetch(USERS_PATH, { method: 'POST', body: { email, roles }, token });
      setEmail('');
      setRoles('USER');
      await loadUsers(token);
    } catch (e: any) {
      setError(e.message);
    }
  };

  const deleteUser = async (id?: string) => {
    if (!token || !id) return;
    try {
      await apiFetch(`${USERS_PATH}/${id}`, { method: 'DELETE', token });
      await loadUsers(token);
    } catch (e: any) {
      setError(e.message);
    }
  };

  const sendResetLink = async (userEmail: string) => {
    if (!token) return;
    setError(null);
    setResettingEmail(userEmail);
    try {
      await apiFetch(RESET_PATH, {
        method: 'POST',
        body: { email: userEmail, recipientName: userEmail },
        token,
      });
      setResetSuccess(`Reset link sent to ${userEmail}`);
    } catch (e: any) {
      setError(e.message);
    } finally {
      setResettingEmail(null);
    }
  };

  return (
    <AdminLayout>
      <Paper sx={{ p: 3 }}>
        <Typography variant="h5" gutterBottom>
          Users
        </Typography>
        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ mb: 3 }}>
          <TextField label="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
          <TextField label="Roles" value={roles} onChange={(e) => setRoles(e.target.value)} />
          <Button variant="contained" onClick={createUser}>
            Create
          </Button>
        </Stack>
        <Box>
          {items.map((u) => (
            <Paper
              key={u.id}
              sx={{
                p: 2,
                mb: 1,
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
              }}
            >
              <div>
                <Typography>{u.email}</Typography>
                <Typography variant="caption" color="text.secondary">
                  {u.roles}
                </Typography>
              </div>
              <Stack direction="row" spacing={1}>
                <Button
                  variant="outlined"
                  size="small"
                  disabled={resettingEmail === u.email}
                  onClick={() => sendResetLink(u.email)}
                >
                  {resettingEmail === u.email ? 'Sending…' : 'Send Reset Link'}
                </Button>
                <Button color="secondary" onClick={() => deleteUser(u.id)}>
                  Delete
                </Button>
              </Stack>
            </Paper>
          ))}
        </Box>
      </Paper>
      <Snackbar
        open={!!resetSuccess}
        autoHideDuration={4000}
        onClose={() => setResetSuccess(null)}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert severity="success" onClose={() => setResetSuccess(null)}>
          {resetSuccess}
        </Alert>
      </Snackbar>
    </AdminLayout>
  );
}
