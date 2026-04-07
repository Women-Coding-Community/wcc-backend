import { useEffect, useState } from 'react';
import {
  Alert,
  Box,
  Button,
  Chip,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Link,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  TextField,
  Typography,
} from '@mui/material';
import AdminLayout from '@/components/AdminLayout';
import { useAuth } from '@/components/AuthProvider';
import { getStoredToken, isTokenExpired } from '@/lib/auth';
import { useRouter } from 'next/router';
import {
  activateMentee,
  getActiveMentees,
  getPendingMentees,
  rejectMenteeByMenteeId,
} from '@/services/menteeService';
import { DashboardMentee } from '@/types/menteeApplication';

function linkedInUrl(mentee: DashboardMentee): string | undefined {
  return mentee.network?.find((n) => n.type === 'LINKEDIN')?.link;
}

export default function MenteesPage() {
  const { token, roles } = useAuth();
  const router = useRouter();

  const [pendingMentees, setPendingMentees] = useState<DashboardMentee[]>([]);
  const [activeMentees, setActiveMentees] = useState<DashboardMentee[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [actionError, setActionError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const [rejectDialogOpen, setRejectDialogOpen] = useState(false);
  const [rejectReason, setRejectReason] = useState('');
  const [rejectTargetId, setRejectTargetId] = useState<number | null>(null);
  const [rejectTargetName, setRejectTargetName] = useState<string>('');

  const canAccess = roles.includes('ADMIN') || roles.includes('MENTORSHIP_ADMIN');

  useEffect(() => {
    const storedToken = getStoredToken();
    if (!storedToken || isTokenExpired(storedToken)) {
      router.replace('/login');
      return;
    }
    if (roles.length > 0 && !canAccess) {
      router.replace('/admin');
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [roles, canAccess]);

  useEffect(() => {
    if (!token || !canAccess) return;
    loadData();
  }, [token, canAccess]);

  async function loadData() {
    if (!token) return;
    setLoading(true);
    setError(null);
    try {
      const [pending, active] = await Promise.all([
        getPendingMentees(token),
        getActiveMentees(token),
      ]);
      setPendingMentees(pending);
      setActiveMentees(active);
    } catch (e: any) {
      setError(e.message ?? 'Failed to load mentees');
    } finally {
      setLoading(false);
    }
  }

  async function handleActivate(menteeId: number) {
    if (!token) return;
    setActionError(null);
    setSubmitting(true);
    try {
      await activateMentee(menteeId, token);
      await loadData();
    } catch (e: any) {
      setActionError(e.message ?? 'Failed to activate mentee');
    } finally {
      setSubmitting(false);
    }
  }

  function openRejectDialog(menteeId: number, menteeName: string) {
    setRejectTargetId(menteeId);
    setRejectTargetName(menteeName);
    setRejectReason('');
    setRejectDialogOpen(true);
  }

  function closeRejectDialog() {
    setRejectDialogOpen(false);
    setRejectTargetId(null);
    setRejectTargetName('');
    setRejectReason('');
  }

  async function handleRejectConfirm() {
    if (!token || !rejectTargetId || rejectReason.trim().length < 50) return;
    setActionError(null);
    setSubmitting(true);
    try {
      await rejectMenteeByMenteeId(rejectTargetId, rejectReason.trim(), token);
      closeRejectDialog();
      await loadData();
    } catch (e: any) {
      setActionError(e.message ?? 'Failed to reject mentee');
    } finally {
      setSubmitting(false);
    }
  }

  if (!canAccess && roles.length > 0) return null;

  return (
    <AdminLayout>
      <Paper sx={{ p: 3, mb: 3 }}>
        <Box display="flex" alignItems="center" gap={1} mb={0.5}>
          <Typography variant="h5">Pending Mentees</Typography>
          {!loading && <Chip label={pendingMentees.length} color="warning" size="small" />}
        </Box>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 2, mt: 0.5 }}>
          Mentees awaiting admin review. Activate to grant access or reject with a reason.
        </Typography>

        {actionError && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {actionError}
          </Alert>
        )}

        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        {loading ? (
          <Box display="flex" justifyContent="center" mt={4}>
            <CircularProgress />
          </Box>
        ) : pendingMentees.length === 0 ? (
          <Typography color="text.secondary">No pending mentees awaiting review.</Typography>
        ) : (
          <Table size="small">
            <TableHead>
              <TableRow sx={{ bgcolor: 'grey.50' }}>
                <TableCell>
                  <strong>Name</strong>
                </TableCell>
                <TableCell>
                  <strong>Email</strong>
                </TableCell>
                <TableCell>
                  <strong>Position</strong>
                </TableCell>
                <TableCell>
                  <strong>Slack</strong>
                </TableCell>
                <TableCell>
                  <strong>Status</strong>
                </TableCell>
                <TableCell align="right">
                  <strong>Actions</strong>
                </TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {pendingMentees.map((mentee) => (
                <TableRow key={mentee.id} hover>
                  <TableCell>{mentee.fullName}</TableCell>
                  <TableCell>{mentee.email}</TableCell>
                  <TableCell>{mentee.position ?? '—'}</TableCell>
                  <TableCell>
                    <Box display="flex" flexDirection="column" gap={0.5}>
                      {mentee.slackDisplayName && (
                        <Chip label={mentee.slackDisplayName} size="small" variant="outlined" />
                      )}
                      {linkedInUrl(mentee) && (
                        <Link
                          href={linkedInUrl(mentee)}
                          target="_blank"
                          rel="noreferrer"
                          variant="body2"
                        >
                          LinkedIn
                        </Link>
                      )}
                    </Box>
                  </TableCell>
                  <TableCell>
                    <Chip label="PENDING" color="warning" size="small" />
                  </TableCell>
                  <TableCell align="right">
                    <Box display="flex" gap={1} justifyContent="flex-end">
                      <Button
                        size="small"
                        variant="contained"
                        color="success"
                        disabled={submitting}
                        onClick={() => handleActivate(mentee.id)}
                      >
                        Activate
                      </Button>
                      <Button
                        size="small"
                        variant="contained"
                        color="error"
                        disabled={submitting}
                        onClick={() => openRejectDialog(mentee.id, mentee.fullName)}
                      >
                        Reject
                      </Button>
                    </Box>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        )}
      </Paper>

      <Paper sx={{ p: 3 }}>
        <Box display="flex" alignItems="center" gap={1} mb={2}>
          <Typography variant="h5">Active Mentees</Typography>
          {!loading && <Chip label={activeMentees.length} color="success" size="small" />}
        </Box>

        {!loading && activeMentees.length === 0 ? (
          <Typography color="text.secondary">No active mentees.</Typography>
        ) : !loading ? (
          <Table size="small">
            <TableHead>
              <TableRow sx={{ bgcolor: 'grey.50' }}>
                <TableCell>
                  <strong>Name</strong>
                </TableCell>
                <TableCell>
                  <strong>Email</strong>
                </TableCell>
                <TableCell>
                  <strong>Position</strong>
                </TableCell>
                <TableCell>
                  <strong>Slack</strong>
                </TableCell>
                <TableCell>
                  <strong>Status</strong>
                </TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {activeMentees.map((mentee) => (
                <TableRow key={mentee.id} hover>
                  <TableCell>{mentee.fullName}</TableCell>
                  <TableCell>{mentee.email}</TableCell>
                  <TableCell>{mentee.position ?? '—'}</TableCell>
                  <TableCell>
                    <Box display="flex" flexDirection="column" gap={0.5}>
                      {mentee.slackDisplayName && (
                        <Chip label={mentee.slackDisplayName} size="small" variant="outlined" />
                      )}
                      {linkedInUrl(mentee) && (
                        <Link
                          href={linkedInUrl(mentee)}
                          target="_blank"
                          rel="noreferrer"
                          variant="body2"
                        >
                          LinkedIn
                        </Link>
                      )}
                    </Box>
                  </TableCell>
                  <TableCell>
                    <Chip label="ACTIVE" color="success" size="small" />
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        ) : null}
      </Paper>

      <Dialog open={rejectDialogOpen} onClose={closeRejectDialog} maxWidth="sm" fullWidth>
        <DialogTitle>Reject Mentee</DialogTitle>
        <DialogContent>
          <Typography variant="body2" sx={{ mb: 1 }}>
            Rejecting: <strong>{rejectTargetName}</strong>
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
            This will set the mentee status to REJECTED and reject all their pending applications. A
            reason is required (minimum 50 characters).
          </Typography>
          <TextField
            autoFocus
            label="Reason for rejection"
            fullWidth
            multiline
            rows={3}
            value={rejectReason}
            onChange={(e) => setRejectReason(e.target.value)}
            inputProps={{ maxLength: 500 }}
            helperText={`${rejectReason.length}/500 (min 50 characters)`}
            sx={{ mt: 1 }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={closeRejectDialog} disabled={submitting}>
            Cancel
          </Button>
          <Button
            onClick={handleRejectConfirm}
            variant="contained"
            color="error"
            disabled={rejectReason.trim().length < 50 || submitting}
          >
            {submitting ? <CircularProgress size={18} /> : 'Confirm Reject'}
          </Button>
        </DialogActions>
      </Dialog>
    </AdminLayout>
  );
}
