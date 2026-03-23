import { useState } from 'react';
import { useRouter } from 'next/router';
import { Alert, Box, Button, Container, Paper, TextField, Typography } from '@mui/material';
import { apiFetch } from '@/lib/api';

export default function ResetPasswordPage() {
  const router = useRouter();
  const { token } = router.query;

  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (newPassword !== confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    if (newPassword.length < 8) {
      setError('Password must be at least 8 characters');
      return;
    }

    if (!token || typeof token !== 'string') {
      setError('Invalid or missing reset token. Please use the link from your email.');
      return;
    }

    setLoading(true);
    try {
      await apiFetch('/api/auth/reset-password/confirm', {
        method: 'POST',
        body: { token, newPassword },
      });
      setSuccess(true);
      setTimeout(() => router.push('/login'), 3000);
    } catch (err: any) {
      setError(err.message || 'Failed to reset password. The link may have expired.');
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <Container maxWidth="sm" sx={{ mt: 12 }}>
        <Paper elevation={3} sx={{ p: 4 }}>
          <Typography variant="h5" color="primary" fontWeight={700} gutterBottom>
            Password Reset Successful
          </Typography>
          <Alert severity="success">
            Your password has been updated. Redirecting you to the login page…
          </Alert>
        </Paper>
      </Container>
    );
  }

  return (
    <Container maxWidth="sm" sx={{ mt: 12 }}>
      <Paper elevation={3} sx={{ p: 4 }}>
        <Typography variant="h4" color="primary" fontWeight={700} gutterBottom>
          Women Coding Community
        </Typography>
        <Typography variant="h6" gutterBottom>
          Set a new password
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
          Choose a strong password for your account.
        </Typography>
        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}
        <Box component="form" onSubmit={handleSubmit}>
          <TextField
            label="New password"
            type="password"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            fullWidth
            required
            margin="normal"
          />
          <TextField
            label="Confirm new password"
            type="password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            fullWidth
            required
            margin="normal"
          />
          <Button
            type="submit"
            variant="contained"
            color="primary"
            fullWidth
            disabled={loading}
            sx={{ mt: 2 }}
          >
            {loading ? 'Saving…' : 'Set new password'}
          </Button>
        </Box>
      </Paper>
    </Container>
  );
}
