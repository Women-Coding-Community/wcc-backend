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
  approveMenteeByMenteeId,
  getPendingPriorityOneReviews,
  rejectMenteeByMenteeId,
} from '@/services/menteeService';
import { MenteeApplicationReview } from '@/types/menteeApplication';

export default function MenteesPage() {
  const { token, roles } = useAuth();
  const router = useRouter();

  const [items, setItems] = useState<MenteeApplicationReview[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [actionError, setActionError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const [rejectDialogOpen, setRejectDialogOpen] = useState(false);
  const [rejectReason, setRejectReason] = useState('');
  const [rejectTargetId, setRejectTargetId] = useState<number | null>(null);

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
  }, [router, roles, canAccess]);

  useEffect(() => {
    if (!token || !canAccess) return;
    loadData();
  }, [token, canAccess]);

  async function loadData() {
    if (!token) return;
    setLoading(true);
    setError(null);
    try {
      const data = await getPendingPriorityOneReviews(token);
      setItems(data);
    } catch (e: any) {
      setError(e.message ?? 'Failed to load pending mentee applications');
    } finally {
      setLoading(false);
    }
  }

  async function handleApprove(menteeId: number) {
    if (!token) return;
    setActionError(null);
    setSubmitting(true);
    try {
      await approveMenteeByMenteeId(menteeId, token);
      await loadData();
    } catch (e: any) {
      setActionError(e.message ?? 'Failed to approve application');
    } finally {
      setSubmitting(false);
    }
  }

  function openRejectDialog(menteeId: number) {
    setRejectTargetId(menteeId);
    setRejectReason('');
    setRejectDialogOpen(true);
  }

  function closeRejectDialog() {
    setRejectDialogOpen(false);
    setRejectTargetId(null);
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
      setActionError(e.message ?? 'Failed to reject application');
    } finally {
      setSubmitting(false);
    }
  }

  if (!canAccess && roles.length > 0) return null;

  return (
    <AdminLayout>
      <Paper sx={{ p: 3 }}>
        <Typography variant="h5" gutterBottom>
          Pending Mentee Applications (Priority 1)
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
        ) : items.length === 0 ? (
          <Typography color="text.secondary">No pending mentee applications.</Typography>
        ) : (
          <Table size="small">
            <TableHead>
              <TableRow sx={{ bgcolor: 'grey.50' }}>
                <TableCell>
                  <strong>App ID</strong>
                </TableCell>
                <TableCell>
                  <strong>Full Name</strong>
                </TableCell>
                <TableCell>
                  <strong>Position</strong>
                </TableCell>
                <TableCell>
                  <strong>Experience</strong>
                </TableCell>
                <TableCell>
                  <strong>Contact</strong>
                </TableCell>
                <TableCell>
                  <strong>Mentorship Goal</strong>
                </TableCell>
                <TableCell align="right">
                  <strong>Actions</strong>
                </TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {items.map((app) => (
                <TableRow key={app.applicationId} hover>
                  <TableCell>{app.applicationId}</TableCell>
                  <TableCell>{app.fullName}</TableCell>
                  <TableCell>{app.position}</TableCell>
                  <TableCell>
                    {app.yearsExperience != null
                      ? `${app.yearsExperience} yr${app.yearsExperience !== 1 ? 's' : ''}`
                      : '—'}
                  </TableCell>
                  <TableCell>
                    <Box display="flex" flexDirection="column" gap={0.5}>
                      <Typography variant="body2">{app.email}</Typography>
                      <Chip label={app.slackDisplayName} size="small" variant="outlined" />
                      {app.linkedinUrl && (
                        <Link
                          href={app.linkedinUrl}
                          target="_blank"
                          rel="noreferrer"
                          variant="body2"
                        >
                          LinkedIn
                        </Link>
                      )}
                    </Box>
                  </TableCell>
                  <TableCell sx={{ maxWidth: 300 }}>
                    <Typography variant="body2" noWrap title={app.mentorshipGoal}>
                      {app.mentorshipGoal}
                    </Typography>
                  </TableCell>
                  <TableCell align="right">
                    <Box display="flex" gap={1} justifyContent="flex-end">
                      <Button
                        size="small"
                        variant="contained"
                        color="success"
                        disabled={submitting}
                        onClick={() => handleApprove(app.menteeId)}
                      >
                        Approve
                      </Button>
                      <Button
                        size="small"
                        variant="contained"
                        color="error"
                        disabled={submitting}
                        onClick={() => openRejectDialog(app.menteeId)}
                      >
                        Reject All
                      </Button>
                    </Box>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        )}
      </Paper>

      <Dialog open={rejectDialogOpen} onClose={closeRejectDialog} maxWidth="sm" fullWidth>
        <DialogTitle>Reject All Applications</DialogTitle>
        <DialogContent>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
            This will reject all pending applications for this mentee. A reason is required.
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
            {submitting ? <CircularProgress size={18} /> : 'Confirm Reject All'}
          </Button>
        </DialogActions>
      </Dialog>
    </AdminLayout>
  );
}
