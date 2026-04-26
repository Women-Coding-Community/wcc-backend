import { useCallback, useEffect, useRef, useState } from 'react';
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
import { getErrorMessage } from '@/lib/api';
import Router from 'next/router';
import { getStoredToken, isTokenExpired } from '@/lib/auth';
import {
  acceptApplication,
  declineApplication,
  getMentorApplications,
} from '@/services/menteeService';
import { MenteeApplication } from '@/types/menteeApplication';
import LinkedInIcon from '@mui/icons-material/LinkedIn';

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

export default function MentorDashboardPage() {
  const { token, member, roles } = useAuth();
  const [assignedApplications, setAssignedApplications] = useState<MenteeApplication[]>([]);
  const [pendingApplications, setPendingApplications] = useState<MenteeApplication[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [actionError, setActionError] = useState<string | null>(null);

  const [declineDialogOpen, setDeclineDialogOpen] = useState(false);
  const [declineReason, setDeclineReason] = useState('');
  const [declineTargetId, setDeclineTargetId] = useState<number | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [expandedMessageId, setExpandedMessageId] = useState<number | null>(null);
  const [expandedBioId, setExpandedBioId] = useState<number | null>(null);
  const [overflowingIds, setOverflowingIds] = useState<Set<number>>(new Set());
  const [overflowingBioIds, setOverflowingBioIds] = useState<Set<number>>(new Set());
  const textRefs = useRef<Map<number, HTMLElement>>(new Map());
  const bioRefs = useRef<Map<number, HTMLElement>>(new Map());

  const mentorId = (member as MemberWithId | null)?.id;
  const canAccess = roles.includes('ADMIN') || roles.includes('MENTOR');

  const loadData = useCallback(async () => {
    if (!token || !mentorId) return;
    setLoading(true);
    setError(null);
    try {
      const allApplications = await getMentorApplications(mentorId, token);
      setAssignedApplications(allApplications.filter((a) => a.status === 'MENTOR_ACCEPTED'));
      setPendingApplications(allApplications.filter((a) => a.status === 'MENTOR_REVIEWING'));
    } catch (error: unknown) {
      setError(getErrorMessage(error, 'Failed to load dashboard data'));
    } finally {
      setLoading(false);
    }
  }, [mentorId, token]);

  useEffect(() => {
    const storedToken = getStoredToken();
    if (!storedToken || isTokenExpired(storedToken)) {
      Router.replace('/login');
      return;
    }
    if (roles.length > 0 && !canAccess) {
      Router.replace('/admin');
    }
  }, [canAccess, roles]);

  useEffect(() => {
    if (!token || !mentorId || !canAccess) return;
    loadData();
  }, [canAccess, loadData, mentorId, token]);

  useEffect(() => {
    const checkOverflow = () => {
      const newOverflowing = new Set<number>();
      textRefs.current.forEach((el, id) => {
        if (el.scrollWidth > el.clientWidth) {
          newOverflowing.add(id);
        }
      });
      setOverflowingIds(newOverflowing);

      const newOverflowingBios = new Set<number>();
      bioRefs.current.forEach((el, id) => {
        if (el.scrollHeight > el.clientHeight) {
          newOverflowingBios.add(id);
        }
      });
      setOverflowingBioIds(newOverflowingBios);
    };
    requestAnimationFrame(checkOverflow);
  }, [pendingApplications, assignedApplications]);

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
    } catch (error: unknown) {
      setActionError(getErrorMessage(error, 'Failed to accept application'));
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
    } catch (error: unknown) {
      setActionError(getErrorMessage(error, 'Failed to decline application'));
    } finally {
      setSubmitting(false);
    }
  }

  if (!canAccess && roles.length > 0) return null;

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

          <Box>
            <Typography variant="h5" fontWeight={600} gutterBottom>
              My Assigned Mentee
            </Typography>
            {assignedApplications.length === 0 ? (
              <Typography color="text.secondary">No assigned mentees yet.</Typography>
            ) : (
              <Stack spacing={2}>
                {assignedApplications.map((app) => {
                  const name = app.menteeName || `Mentee #${app.menteeId}`;
                  return (
                    <Paper key={app.applicationId} variant="outlined" sx={{ p: 2 }}>
                      <Stack
                        direction="row"
                        alignItems="center"
                        justifyContent="space-between"
                        flexWrap="wrap"
                        gap={1}
                      >
                        <Stack direction="row" alignItems="flex-start" spacing={2}>
                          <Avatar sx={{ bgcolor: 'primary.main' }}>{getInitials(name)}</Avatar>
                          <Box>
                            <Stack direction="row" alignItems="center" spacing={1}>
                              <Typography fontWeight={600}>{name}</Typography>
                              {app.menteeLinkedIn && (
                                <Button
                                  href={app.menteeLinkedIn}
                                  target="_blank"
                                  rel="noopener noreferrer"
                                  size="small"
                                  sx={{ minWidth: 'auto', p: 0.5 }}
                                >
                                  <LinkedInIcon fontSize="small" sx={{ color: '#0077b5' }} />
                                </Button>
                              )}
                              <Chip
                                label="Active"
                                size="small"
                                sx={{
                                  bgcolor: 'success.light',
                                  color: 'success.dark',
                                  fontWeight: 600,
                                }}
                              />
                            </Stack>
                            {app.menteeBio && (
                              <Box sx={{ mt: 0.5 }}>
                                {expandedBioId === app.applicationId ? (
                                  <>
                                    <Typography
                                      variant="body2"
                                      color="text.secondary"
                                      sx={{ whiteSpace: 'pre-wrap' }}
                                    >
                                      {app.menteeBio}
                                    </Typography>
                                    <Button
                                      size="small"
                                      onClick={() => setExpandedBioId(null)}
                                      sx={{ p: 0, minWidth: 'auto', textTransform: 'none' }}
                                    >
                                      Show less
                                    </Button>
                                  </>
                                ) : (
                                  <>
                                    <Typography
                                      variant="body2"
                                      color="text.secondary"
                                      sx={{
                                        display: '-webkit-box',
                                        WebkitLineClamp: 2,
                                        WebkitBoxOrient: 'vertical',
                                        overflow: 'hidden',
                                      }}
                                      ref={(el) => {
                                        if (el) {
                                          bioRefs.current.set(app.applicationId, el);
                                        } else {
                                          bioRefs.current.delete(app.applicationId);
                                        }
                                      }}
                                    >
                                      {app.menteeBio}
                                    </Typography>
                                    {overflowingBioIds.has(app.applicationId) && (
                                      <Button
                                        size="small"
                                        onClick={() => setExpandedBioId(app.applicationId)}
                                        sx={{ p: 0, minWidth: 'auto', textTransform: 'none' }}
                                      >
                                        Show more
                                      </Button>
                                    )}
                                  </>
                                )}
                              </Box>
                            )}
                          </Box>
                        </Stack>
                        <Stack direction="row" spacing={1} alignItems="center">
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

          <Box>
            <Typography variant="h5" fontWeight={600} gutterBottom>
              Mentee Applications
            </Typography>
            <Typography variant="body2" color="text.secondary" mb={2}>
              Review and manage mentee applications assigned to you
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
                        <strong>Bio</strong>
                      </TableCell>
                      <TableCell>
                        <strong>Message</strong>
                      </TableCell>
                      <TableCell>
                        <strong>Applied</strong>
                      </TableCell>
                      <TableCell align="right">
                        <strong>Action</strong>
                      </TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {pendingApplications.map((app) => {
                      const name = app.menteeName || `Mentee #${app.menteeId}`;
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
                              {app.menteeLinkedIn && (
                                <Button
                                  href={app.menteeLinkedIn}
                                  target="_blank"
                                  rel="noopener noreferrer"
                                  size="small"
                                  sx={{ minWidth: 'auto', p: 0.5 }}
                                >
                                  <LinkedInIcon fontSize="small" sx={{ color: '#0077b5' }} />
                                </Button>
                              )}
                            </Stack>
                          </TableCell>
                          <TableCell sx={{ maxWidth: 250 }}>
                            {app.menteeBio ? (
                              expandedBioId === app.applicationId ? (
                                <Box>
                                  <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap' }}>
                                    {app.menteeBio}
                                  </Typography>
                                  <Button
                                    size="small"
                                    onClick={() => setExpandedBioId(null)}
                                    sx={{ p: 0, minWidth: 'auto', textTransform: 'none' }}
                                  >
                                    Show less
                                  </Button>
                                </Box>
                              ) : (
                                <Box>
                                  <Typography
                                    variant="body2"
                                    ref={(el) => {
                                      if (el) {
                                        bioRefs.current.set(app.applicationId, el);
                                      } else {
                                        bioRefs.current.delete(app.applicationId);
                                      }
                                    }}
                                    sx={{
                                      display: '-webkit-box',
                                      WebkitLineClamp: 2,
                                      WebkitBoxOrient: 'vertical',
                                      overflow: 'hidden',
                                    }}
                                  >
                                    {app.menteeBio}
                                  </Typography>
                                  {overflowingBioIds.has(app.applicationId) && (
                                    <Button
                                      size="small"
                                      onClick={() => setExpandedBioId(app.applicationId)}
                                      sx={{ p: 0, minWidth: 'auto', textTransform: 'none' }}
                                    >
                                      Show more
                                    </Button>
                                  )}
                                </Box>
                              )
                            ) : (
                              <Typography variant="body2" color="text.secondary">
                                -
                              </Typography>
                            )}
                          </TableCell>
                          <TableCell sx={{ maxWidth: 300 }}>
                            {expandedMessageId === app.applicationId ? (
                              <Box>
                                <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap' }}>
                                  {app.whyMentor}
                                </Typography>
                                <Button
                                  size="small"
                                  onClick={() => setExpandedMessageId(null)}
                                  sx={{ p: 0, minWidth: 'auto', textTransform: 'none' }}
                                >
                                  Show less
                                </Button>
                              </Box>
                            ) : (
                              <Box>
                                <Typography
                                  variant="body2"
                                  noWrap
                                  title={app.whyMentor}
                                  ref={(el) => {
                                    if (el) {
                                      textRefs.current.set(app.applicationId, el);
                                    } else {
                                      textRefs.current.delete(app.applicationId);
                                    }
                                  }}
                                >
                                  {app.whyMentor}
                                </Typography>
                                {overflowingIds.has(app.applicationId) && (
                                  <Button
                                    size="small"
                                    onClick={() => setExpandedMessageId(app.applicationId)}
                                    sx={{ p: 0, minWidth: 'auto', textTransform: 'none' }}
                                  >
                                    Show more
                                  </Button>
                                )}
                              </Box>
                            )}
                          </TableCell>
                          <TableCell>
                            <Typography variant="body2">
                              {new Date(app.appliedAt).toLocaleDateString()}
                            </Typography>
                          </TableCell>
                          <TableCell align="right">
                            <Stack direction="row" spacing={1} justifyContent="flex-end">
                              <Button
                                size="small"
                                variant="contained"
                                color="success"
                                disabled={submitting}
                                onClick={() => handleAccept(app.applicationId)}
                              >
                                Accept
                              </Button>
                              <Button
                                size="small"
                                variant="contained"
                                color="error"
                                disabled={submitting}
                                onClick={() => openDeclineDialog(app.applicationId)}
                              >
                                Decline
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
