import { useEffect, useState } from 'react';
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
  DialogTitle,
  Divider,
  Paper,
  Stack,
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
import { useRouter } from 'next/router';
import { getStoredToken, isTokenExpired } from '@/lib/auth';
import {
  acceptApplication,
  declineApplication,
  getMentees,
  getMentorApplications,
} from '@/services/menteeService';
import { DashboardMentee, MenteeApplication } from '@/types/menteeApplication';

interface MemberWithId {
  id: number;
  fullName?: string;
}

function getInitials(name: string): string {
  return name
    .split(' ')
    .map((n) => n[0])
    .join('')
    .toUpperCase()
    .slice(0, 2);
}

function formatFocus(focus?: string[]): string {
  if (!focus?.length) return '—';
  return focus[0]
    .replace(/_/g, ' ')
    .toLowerCase()
    .replace(/\b\w/g, (l) => l.toUpperCase());
}

function formatMentorshipType(types?: string[]): string {
  if (!types?.length) return '—';
  if (types.includes('LONG_TERM')) return 'Long-term';
  if (types.includes('AD_HOC')) return 'Ad-hoc';
  return types[0];
}

function formatYears(years?: number): string {
  if (years == null) return '—';
  if (years === 0) return 'Less than 1 year';
  if (years === 1) return '1 year';
  if (years <= 3) return '1-3 years';
  if (years <= 5) return '3-5 years';
  return `${years}+ years`;
}

function formatTechStack(mentee: DashboardMentee): string {
  const lang = mentee.skills?.languages?.[0]?.language;
  const area = mentee.skills?.areas?.[0]?.technicalArea?.replace(/_/g, ' ');
  return lang ?? area ?? '—';
}

const ACTIVE_STATUSES = ['PENDING', 'MENTOR_REVIEWING'];

export default function MentorDashboardPage() {
  const { token, member, roles } = useAuth();
  const router = useRouter();

  const [assignedApplications, setAssignedApplications] = useState<MenteeApplication[]>([]);
  const [pendingApplications, setPendingApplications] = useState<MenteeApplication[]>([]);
  const [menteesMap, setMenteesMap] = useState<Map<number, DashboardMentee>>(new Map());
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [actionError, setActionError] = useState<string | null>(null);

  const [declineDialogOpen, setDeclineDialogOpen] = useState(false);
  const [declineReason, setDeclineReason] = useState('');
  const [declineTargetId, setDeclineTargetId] = useState<number | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const mentorId = (member as MemberWithId | null)?.id;

  useEffect(() => {
    const storedToken = getStoredToken();
    if (!storedToken || isTokenExpired(storedToken)) {
      router.replace('/login');
    }
  }, [router]);

  useEffect(() => {
    if (!token || !mentorId) return;
    loadData();
  }, [token, mentorId]);

  async function loadData() {
    if (!token || !mentorId) return;
    setLoading(true);
    setError(null);
    try {
      const [allApplications, mentees] = await Promise.all([
        getMentorApplications(mentorId, token),
        getMentees(token),
      ]);

      const map = new Map<number, DashboardMentee>();
      mentees.forEach((m) => map.set(m.id, m));
      setMenteesMap(map);

      setAssignedApplications(allApplications.filter((a) => a.status === 'MATCHED'));
      setPendingApplications(allApplications.filter((a) => ACTIVE_STATUSES.includes(a.status)));
    } catch (e: any) {
      setError(e.message ?? 'Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  }

  function openDeclineDialog(applicationId: number) {
    setDeclineTargetId(applicationId);
    setDeclineReason('');
    setDeclineDialogOpen(true);
  }

  function closeDeclineDialog() {
    setDeclineDialogOpen(false);
    setDeclineTargetId(null);
    setDeclineReason('');
  }

  async function handleAccept(applicationId: number) {
    if (!token) return;
    setActionError(null);
    setSubmitting(true);
    try {
      await acceptApplication(applicationId, token);
      await loadData();
    } catch (e: any) {
      setActionError(e.message ?? 'Failed to accept application');
    } finally {
      setSubmitting(false);
    }
  }

  async function handleDeclineConfirm() {
    if (!token || !declineTargetId || !declineReason.trim()) return;
    setActionError(null);
    setSubmitting(true);
    try {
      await declineApplication(declineTargetId, declineReason.trim(), token);
      closeDeclineDialog();
      await loadData();
    } catch (e: any) {
      setActionError(e.message ?? 'Failed to decline application');
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <AdminLayout>
      {loading ? (
        <Box display="flex" justifyContent="center" mt={6}>
          <CircularProgress />
        </Box>
      ) : error ? (
        <Alert severity="error">{error}</Alert>
      ) : (
        <Stack spacing={4}>
          {actionError && <Alert severity="error">{actionError}</Alert>}

          {/* My Assigned Mentee */}
          <Box>
            <Typography variant="h5" fontWeight={600} gutterBottom>
              My Assigned Mentee
            </Typography>
            {assignedApplications.length === 0 ? (
              <Typography color="text.secondary">No assigned mentees yet.</Typography>
            ) : (
              <Stack spacing={2}>
                {assignedApplications.map((app) => {
                  const mentee = menteesMap.get(app.menteeId);
                  const name = mentee?.fullName ?? `Mentee #${app.menteeId}`;
                  const type = formatMentorshipType(mentee?.skills?.mentorshipType);

                  return (
                    <Paper key={app.applicationId} variant="outlined" sx={{ p: 2 }}>
                      <Stack
                        direction="row"
                        alignItems="center"
                        justifyContent="space-between"
                        flexWrap="wrap"
                        gap={1}
                      >
                        <Stack direction="row" alignItems="center" spacing={2}>
                          <Avatar sx={{ bgcolor: 'primary.main' }}>{getInitials(name)}</Avatar>
                          <Box>
                            <Typography fontWeight={600}>{name}</Typography>
                            {mentee?.position && (
                              <Typography variant="caption" color="text.secondary">
                                {mentee.position}
                              </Typography>
                            )}
                          </Box>
                          <Chip
                            label="Active"
                            size="small"
                            sx={{
                              bgcolor: 'success.light',
                              color: 'success.dark',
                              fontWeight: 600,
                            }}
                          />
                          {type !== '—' && (
                            <Chip label={type} size="small" color="primary" variant="outlined" />
                          )}
                        </Stack>
                        <Stack direction="row" spacing={1} alignItems="center">
                          {mentee?.email && (
                            <Button
                              size="small"
                              variant="contained"
                              href={`mailto:${mentee.email}`}
                            >
                              Email Session
                            </Button>
                          )}
                          <Button size="small" variant="outlined">
                            Session History
                          </Button>
                          <Button size="small" color="primary">
                            View Details
                          </Button>
                        </Stack>
                      </Stack>
                    </Paper>
                  );
                })}
              </Stack>
            )}
          </Box>

          <Divider />

          {/* Mentee Applications */}
          <Box>
            <Typography variant="h5" fontWeight={600} gutterBottom>
              Mentee Applications
            </Typography>
            <Typography variant="body2" color="text.secondary" mb={2}>
              Review and manage mentee applications
            </Typography>

            {pendingApplications.length === 0 ? (
              <Typography color="text.secondary">No pending applications.</Typography>
            ) : (
              <Paper variant="outlined">
                <Table size="small">
                  <TableHead>
                    <TableRow sx={{ bgcolor: 'grey.50' }}>
                      <TableCell>
                        <strong>Mentee</strong>
                      </TableCell>
                      <TableCell>
                        <strong>Tech Stack</strong>
                      </TableCell>
                      <TableCell>
                        <strong>Mentorship Focus</strong>
                      </TableCell>
                      <TableCell>
                        <strong>Years of Experience</strong>
                      </TableCell>
                      <TableCell>
                        <strong>Type</strong>
                      </TableCell>
                      <TableCell align="right">
                        <strong>Action</strong>
                      </TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {pendingApplications.map((app) => {
                      const mentee = menteesMap.get(app.menteeId);
                      const name = mentee?.fullName ?? `Mentee #${app.menteeId}`;
                      const type = formatMentorshipType(mentee?.skills?.mentorshipType);

                      return (
                        <TableRow key={app.applicationId} hover>
                          <TableCell>
                            <Stack direction="row" alignItems="center" spacing={1}>
                              <Avatar
                                sx={{
                                  width: 32,
                                  height: 32,
                                  fontSize: 13,
                                  bgcolor: 'secondary.main',
                                }}
                              >
                                {getInitials(name)}
                              </Avatar>
                              <Typography variant="body2">{name}</Typography>
                            </Stack>
                          </TableCell>
                          <TableCell>
                            <Typography variant="body2">
                              {formatTechStack(
                                mentee ?? {
                                  id: app.menteeId,
                                  fullName: name,
                                }
                              )}
                            </Typography>
                          </TableCell>
                          <TableCell>
                            <Typography variant="body2">
                              {formatFocus(mentee?.skills?.mentorshipFocus)}
                            </Typography>
                          </TableCell>
                          <TableCell>
                            <Typography variant="body2">
                              {formatYears(mentee?.skills?.yearsExperience)}
                            </Typography>
                          </TableCell>
                          <TableCell>
                            <Chip
                              label={type}
                              size="small"
                              color={type === 'Long-term' ? 'primary' : 'warning'}
                              variant="outlined"
                            />
                          </TableCell>
                          <TableCell align="right">
                            <Stack direction="row" spacing={1} justifyContent="flex-end">
                              <Button size="small" variant="outlined">
                                View Profile
                              </Button>
                              <Button
                                size="small"
                                variant="contained"
                                color="success"
                                disabled={submitting}
                                onClick={() => handleAccept(app.applicationId)}
                              >
                                Approve
                              </Button>
                              <Button
                                size="small"
                                variant="contained"
                                color="error"
                                disabled={submitting}
                                onClick={() => openDeclineDialog(app.applicationId)}
                              >
                                Reject
                              </Button>
                            </Stack>
                          </TableCell>
                        </TableRow>
                      );
                    })}
                  </TableBody>
                </Table>
              </Paper>
            )}
          </Box>
        </Stack>
      )}

      {/* Decline reason dialog */}
      <Dialog open={declineDialogOpen} onClose={closeDeclineDialog} maxWidth="sm" fullWidth>
        <DialogTitle>Decline Application</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            label="Reason for declining"
            fullWidth
            multiline
            rows={3}
            value={declineReason}
            onChange={(e) => setDeclineReason(e.target.value)}
            inputProps={{ maxLength: 500 }}
            helperText={`${declineReason.length}/500`}
            sx={{ mt: 1 }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={closeDeclineDialog} disabled={submitting}>
            Cancel
          </Button>
          <Button
            onClick={handleDeclineConfirm}
            variant="contained"
            color="error"
            disabled={!declineReason.trim() || submitting}
          >
            {submitting ? <CircularProgress size={18} /> : 'Confirm Decline'}
          </Button>
        </DialogActions>
      </Dialog>
    </AdminLayout>
  );
}
