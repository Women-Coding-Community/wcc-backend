import { useState } from 'react';
import { useRouter } from 'next/router';
import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Container,
  Paper,
  TextField,
  Typography,
} from '@mui/material';
import { apiFetch, getErrorMessage } from '@/lib/api';

interface PasswordResetResponse {
  message: string;
}

export default function ResetPasswordPage() {
  const router = useRouter();
  const { token } = router.query;

  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (newPassword !== confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    if (!token || typeof token !== 'string') {
      setError('Reset token is missing or invalid. Please request a new password reset link.');
      return;
    }

    setLoading(true);
    try {
      await apiFetch<PasswordResetResponse>('/api/auth/reset-password/confirm', {
        method: 'POST',
        body: { token, newPassword },
      });
      setSuccess(true);
      setTimeout(() => router.push('/login'), 3000);
    } catch (err: unknown) {
      setError(getErrorMessage(err, 'Failed to reset password. The link may have expired.'));
    } finally {
      setLoading(false);
    }
  };

  const isInvalidToken = router.isReady && !token;

  return (
    <Container maxWidth="sm" sx={{ mt: 12 }}>
      <Paper elevation={3} sx={{ p: 4 }}>
        <Typography variant="h4" color="primary" fontWeight={700} gutterBottom>
          Women Coding Community
        </Typography>
        <Typography variant="h6" gutterBottom>
          Reset Your Password
        </Typography>

        {isInvalidToken && (
          <Alert severity="error" sx={{ mb: 2 }}>
            No reset token found. Please use the link from your reset email or request a new one.
          </Alert>
        )}

        {success ? (
          <Alert severity="success" sx={{ mb: 2 }}>
            Your password has been reset successfully. Redirecting to login…
          </Alert>
        ) : (
          <>
            {error && (
              <Alert severity="error" sx={{ mb: 2 }}>
                {error}
              </Alert>
            )}
            <Box component="form" onSubmit={handleSubmit}>
              <TextField
                label="New Password"
                type="password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                fullWidth
                required
                margin="normal"
                inputProps={{ minLength: 8, maxLength: 128 }}
                helperText="Must be 8–128 characters"
                disabled={isInvalidToken || loading}
              />
              <TextField
                label="Confirm New Password"
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                fullWidth
                required
                margin="normal"
                disabled={isInvalidToken || loading}
              />
              <Button
                type="submit"
                variant="contained"
                color="primary"
                fullWidth
                disabled={isInvalidToken || loading}
                sx={{ mt: 2 }}
                startIcon={loading ? <CircularProgress size={18} color="inherit" /> : null}
              >
                {loading ? 'Resetting…' : 'Reset Password'}
              </Button>
            </Box>
          </>
        )}
      </Paper>
    </Container>
  );
}
