import { useCallback, useEffect, useState } from 'react';
import {
  Alert,
  Avatar,
  Box,
  Button,
  Chip,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Link,
  Paper,
  Snackbar,
  Stack,
  Typography,
} from '@mui/material';
import AdminLayout from '@/components/AdminLayout';
import { apiFetch, getErrorMessage } from '@/lib/api';
import { getStoredToken, isTokenExpired } from '@/lib/auth';
import { useRouter } from 'next/router';

interface MemberCountry {
  countryCode?: string;
  countryName?: string;
}

interface MemberNetwork {
  type: string;
  link: string;
}

interface MemberItem {
  id: number | string;
  fullName: string;
  position?: string;
  email?: string;
  slackDisplayName?: string;
  country?: MemberCountry;
  city?: string;
  companyName?: string;
  memberTypes?: string[];
  images?: unknown[];
  network?: MemberNetwork[];
}

type MembersResponse =
  | MemberItem[]
  | {
      items?: MemberItem[];
      content?: MemberItem[];
      data?: MemberItem[];
    };

const MEMBERS_PATH = '/api/platform/v1/members';
const RESET_PASSWORD_PATH = '/api/auth/reset-password/request';

interface ResetDialogState {
  open: boolean;
  member: MemberItem | null;
}

export default function MembersPage() {
  const router = useRouter();
  const [items, setItems] = useState<MemberItem[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [resetDialog, setResetDialog] = useState<ResetDialogState>({ open: false, member: null });
  const [resetLoading, setResetLoading] = useState(false);
  const [snackbar, setSnackbar] = useState<{
    open: boolean;
    message: string;
    severity: 'success' | 'error';
  }>({
    open: false,
    message: '',
    severity: 'success',
  });

  const normalize = useCallback((resp: MembersResponse): MemberItem[] => {
    if (Array.isArray(resp)) return resp;
    if (resp?.items && Array.isArray(resp.items)) return resp.items;
    if (resp?.content && Array.isArray(resp.content)) return resp.content;
    if (resp?.data && Array.isArray(resp.data)) return resp.data;
    return [];
  }, []);

  const load = useCallback(
    async (token: string) => {
      try {
        const data = await apiFetch<MembersResponse>(MEMBERS_PATH, { token });
        setItems(normalize(data));
      } catch (error: unknown) {
        setError(getErrorMessage(error, 'Failed to load members'));
      }
    },
    [normalize]
  );

  useEffect(() => {
    const storedToken = getStoredToken();
    if (!storedToken || isTokenExpired(storedToken)) router.replace('/login');
    else {
      setToken(storedToken);
      load(storedToken);
    }
  }, [load, router]);

  const openResetDialog = (member: MemberItem) => {
    setResetDialog({ open: true, member });
  };

  const closeResetDialog = () => {
    setResetDialog({ open: false, member: null });
  };

  const handleSendResetEmail = async () => {
    const member = resetDialog.member;
    if (!member?.email || !token) return;

    setResetLoading(true);
    try {
      await apiFetch(RESET_PASSWORD_PATH, {
        method: 'POST',
        token,
        body: { email: member.email, recipientName: member.fullName },
      });
      setSnackbar({
        open: true,
        message: `Reset password email sent to ${member.email}`,
        severity: 'success',
      });
    } catch (err: unknown) {
      setSnackbar({
        open: true,
        message: getErrorMessage(err, 'Failed to send reset email'),
        severity: 'error',
      });
    } finally {
      setResetLoading(false);
      closeResetDialog();
    }
  };

  const prettyLocation = (m: MemberItem) => {
    const parts = [m.city, m.country?.countryName || m.country?.countryCode].filter(Boolean);
    return parts.join(', ');
  };

  const handleCreateMember = () => {
    router.push('/admin/members/create');
  };

  return (
    <AdminLayout>
      <Paper sx={{ p: 3 }}>
        <Box
          sx={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            mb: 2,
          }}
        >
          <Typography variant="h5">Members</Typography>
          <Button variant="contained" color="primary" onClick={handleCreateMember}>
            Create New Member
          </Button>
        </Box>

        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        <Box>
          {items.map((m) => (
            <Paper key={m.id} sx={{ p: 2, mb: 2 }}>
              <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} alignItems="flex-start">
                <Avatar sx={{ bgcolor: 'primary.main', width: 50, height: 50 }}>
                  {(m.fullName || '?').substring(0, 1)}
                </Avatar>
                <Box sx={{ flex: 1 }}>
                  <Typography variant="subtitle1" sx={{ fontWeight: 600 }}>
                    {m.id} - {m.fullName}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {[m.position, m.companyName].filter(Boolean).join(' @ ')}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {prettyLocation(m)}
                  </Typography>

                  {/* Email & Slack */}
                  <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ mt: 1 }}>
                    {m.email && <Typography variant="body2">Email: {m.email}</Typography>}
                    {m.slackDisplayName && (
                      <Typography variant="body2">Slack: {m.slackDisplayName}</Typography>
                    )}
                  </Stack>

                  {/* Member Types */}
                  {m.memberTypes && m.memberTypes.length > 0 && (
                    <Stack direction="row" spacing={1} flexWrap="wrap" sx={{ mt: 1 }}>
                      {m.memberTypes.map((t) => (
                        <Chip key={`type-${m.id}-${t}`} label={t} size="small" color="secondary" />
                      ))}
                    </Stack>
                  )}

                  {/* Network */}
                  {m.network && m.network.length > 0 && (
                    <Stack direction="row" spacing={2} flexWrap="wrap" sx={{ mt: 1 }}>
                      {m.network.map((n) => (
                        <Link
                          key={`network-${m.id}-${n.type}`}
                          href={n.link}
                          target="_blank"
                          rel="noopener noreferrer"
                        >
                          {n.type}
                        </Link>
                      ))}
                    </Stack>
                  )}
                </Box>

                {/* Actions */}
                {m.email && (
                  <Box sx={{ display: 'flex', alignItems: 'flex-start', pt: 0.5 }}>
                    <Button
                      variant="outlined"
                      size="small"
                      color="warning"
                      onClick={() => openResetDialog(m)}
                    >
                      Reset Password
                    </Button>
                  </Box>
                )}
              </Stack>
            </Paper>
          ))}
          {items.length === 0 && !error && (
            <Typography color="text.secondary">No members found.</Typography>
          )}
        </Box>
      </Paper>

      {/* Confirm reset password dialog */}
      <Dialog open={resetDialog.open} onClose={closeResetDialog}>
        <DialogTitle>Send Password Reset Email</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Send a password reset link to <strong>{resetDialog.member?.email}</strong> for{' '}
            <strong>{resetDialog.member?.fullName}</strong>?
            <br />
            The link will expire in 60 minutes.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={closeResetDialog} disabled={resetLoading}>
            Cancel
          </Button>
          <Button
            onClick={handleSendResetEmail}
            variant="contained"
            color="warning"
            disabled={resetLoading}
            startIcon={resetLoading ? <CircularProgress size={16} color="inherit" /> : null}
          >
            {resetLoading ? 'Sending…' : 'Send Reset Email'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Success / error snackbar */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={5000}
        onClose={() => setSnackbar((s) => ({ ...s, open: false }))}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert
          severity={snackbar.severity}
          onClose={() => setSnackbar((s) => ({ ...s, open: false }))}
          sx={{ width: '100%' }}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </AdminLayout>
  );
}
