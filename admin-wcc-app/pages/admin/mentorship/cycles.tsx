import { useCallback, useEffect, useState } from 'react';

import {
  Alert,
  Box,
  Button,
  Chip,
  ChipProps,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  MenuItem,
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
import { getErrorMessage } from '@/lib/api';
import { getStoredToken, isTokenExpired } from '@/lib/auth';
import { CYCLE_STATUSES } from '@/lib/cycleStatuses';
import Router from 'next/router';
import { getCycles, updateCycleStatus } from '@/services/mentorshipService';
import { CycleStatus, MentorshipCycle } from '@/types/mentorship';

const ALL = 'ALL';
const CURRENT_YEAR = new Date().getFullYear();

const STATUS_COLORS: Record<CycleStatus, ChipProps['color']> = {
  DRAFT: 'default',
  OPEN: 'success',
  CLOSED: 'warning',
  IN_PROGRESS: 'info',
  COMPLETED: 'default',
  CANCELLED: 'error',
};

function statusDescription(value: CycleStatus): string {
  return CYCLE_STATUSES.find((status) => status.value === value)?.description ?? '';
}

function cycleName(cycle: MentorshipCycle): string {
  const month = cycle.cycleMonth.charAt(0) + cycle.cycleMonth.slice(1).toLowerCase();
  return `${month} ${cycle.cycleYear}`;
}

function formatDate(date: string): string {
  return new Date(date).toLocaleDateString('en-GB', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
    timeZone: 'UTC',
  });
}

function dateRange(start: string, end: string): string {
  return `${formatDate(start)} – ${formatDate(end)}`;
}

export default function CyclesPage() {
  const { token, roles } = useAuth();
  const [cycles, setCycles] = useState<MentorshipCycle[]>([]);
  const [loading, setLoading] = useState(true);
  const [typeFilter, setTypeFilter] = useState(ALL);
  const [statusFilter, setStatusFilter] = useState(ALL);
  const [pendingChange, setPendingChange] = useState<{
    cycle: MentorshipCycle;
    status: CycleStatus;
  } | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const canAccess = roles.includes('ADMIN') || roles.includes('MENTORSHIP_ADMIN');

  const loadCycles = useCallback(async () => {
    if (!token) return;
    setLoading(true);
    setError(null);
    try {
      setCycles(await getCycles(CURRENT_YEAR, token));
    } catch (error: unknown) {
      setError(getErrorMessage(error, 'Failed to load cycles'));
    } finally {
      setLoading(false);
    }
  }, [token]);

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
    if (!token || !canAccess) return;
    loadCycles();
  }, [canAccess, loadCycles, token]);

  const mentorshipTypes = Array.from(new Set(cycles.map((cycle) => cycle.mentorshipType)));

  const shownCycles = cycles.filter(
    (cycle) =>
      (typeFilter === ALL || cycle.mentorshipType === typeFilter) &&
      (statusFilter === ALL || cycle.status === statusFilter)
  );

  function closeDialog() {
    setPendingChange(null);
  }

  async function handleConfirm() {
    if (!token || !pendingChange) return;
    setError(null);
    setSuccess(null);
    setSubmitting(true);
    try {
      const updated = await updateCycleStatus(
        pendingChange.cycle.cycleId,
        pendingChange.status,
        token
      );
      setCycles((current) =>
        current.map((cycle) => (cycle.cycleId === updated.cycleId ? updated : cycle))
      );
      setSuccess(`${cycleName(updated)} · ${updated.mentorshipType} is now ${updated.status}.`);
    } catch (error: unknown) {
      setError(getErrorMessage(error, 'Failed to update cycle status'));
    } finally {
      setSubmitting(false);
      closeDialog();
    }
  }

  if (!canAccess && roles.length > 0) return null;

  return (
    <AdminLayout>
      <Paper sx={{ p: 3 }}>
        <Box display="flex" alignItems="center" gap={1}>
          <Typography variant="h5">Mentorship Cycles</Typography>
          {!loading && <Chip label={cycles.length} color="primary" size="small" />}
        </Box>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 2, mt: 0.5 }}>
          Cycles for {CURRENT_YEAR}.
        </Typography>

        <Box display="flex" alignItems="center" gap={2} flexWrap="wrap" mb={2}>
          <TextField
            select
            size="small"
            label="Type"
            value={typeFilter}
            onChange={(e) => setTypeFilter(e.target.value)}
            sx={{ minWidth: 180 }}
          >
            <MenuItem value={ALL}>All</MenuItem>
            {mentorshipTypes.map((type) => (
              <MenuItem key={type} value={type}>
                {type}
              </MenuItem>
            ))}
          </TextField>
          <TextField
            select
            size="small"
            label="Status"
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            sx={{ minWidth: 180 }}
          >
            <MenuItem value={ALL}>All</MenuItem>
            {CYCLE_STATUSES.map((status) => (
              <MenuItem key={status.value} value={status.value}>
                {status.label}
              </MenuItem>
            ))}
          </TextField>
        </Box>

        {error && (
          <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
            {error}
          </Alert>
        )}
        {success && (
          <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess(null)}>
            {success}
          </Alert>
        )}

        {loading ? (
          <Box display="flex" justifyContent="center" mt={4}>
            <CircularProgress />
          </Box>
        ) : cycles.length === 0 ? (
          <Typography color="text.secondary">No cycles for {CURRENT_YEAR}.</Typography>
        ) : shownCycles.length === 0 ? (
          <Typography color="text.secondary">No cycles match these filters.</Typography>
        ) : (
          <Box sx={{ overflowX: 'auto' }}>
            <Table size="small">
              <TableHead>
                <TableRow sx={{ bgcolor: 'grey.50' }}>
                  <TableCell>
                    <strong>Cycle</strong>
                  </TableCell>
                  <TableCell>
                    <strong>Type</strong>
                  </TableCell>
                  <TableCell>
                    <strong>Registration</strong>
                  </TableCell>
                  <TableCell>
                    <strong>Runs</strong>
                  </TableCell>
                  <TableCell>
                    <strong>Status</strong>
                  </TableCell>
                  <TableCell align="right">
                    <strong>Change status</strong>
                  </TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {shownCycles.map((cycle) => (
                  <TableRow key={cycle.cycleId} hover>
                    <TableCell>{cycleName(cycle)}</TableCell>
                    <TableCell>{cycle.mentorshipType}</TableCell>
                    <TableCell>
                      {dateRange(cycle.registrationStartDate, cycle.registrationEndDate)}
                    </TableCell>
                    <TableCell>{dateRange(cycle.cycleStartDate, cycle.cycleEndDate)}</TableCell>
                    <TableCell>
                      <Chip label={cycle.status} color={STATUS_COLORS[cycle.status]} size="small" />
                    </TableCell>
                    <TableCell align="right">
                      <TextField
                        select
                        size="small"
                        value={cycle.status}
                        onChange={(e) =>
                          setPendingChange({ cycle, status: e.target.value as CycleStatus })
                        }
                        inputProps={{ 'aria-label': `Change status of ${cycleName(cycle)}` }}
                        sx={{ minWidth: 160, textAlign: 'left' }}
                      >
                        {CYCLE_STATUSES.map((status) => (
                          <MenuItem key={status.value} value={status.value}>
                            {status.label}
                          </MenuItem>
                        ))}
                      </TextField>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </Box>
        )}
      </Paper>

      <Dialog open={pendingChange !== null} onClose={closeDialog} maxWidth="xs" fullWidth>
        <DialogTitle>Change cycle status?</DialogTitle>
        {pendingChange && (
          <DialogContent>
            <Typography variant="body2" sx={{ mb: 2 }}>
              <strong>
                {cycleName(pendingChange.cycle)} · {pendingChange.cycle.mentorshipType}
              </strong>
            </Typography>
            <Box display="flex" alignItems="center" gap={1} mb={2}>
              <Chip
                label={pendingChange.cycle.status}
                color={STATUS_COLORS[pendingChange.cycle.status]}
                size="small"
              />
              <Typography aria-hidden>→</Typography>
              <Chip
                label={pendingChange.status}
                color={STATUS_COLORS[pendingChange.status]}
                size="small"
              />
            </Box>
            <Typography variant="body2" color="text.secondary">
              {statusDescription(pendingChange.status)}.
            </Typography>
          </DialogContent>
        )}
        <DialogActions>
          <Button onClick={closeDialog} disabled={submitting}>
            Cancel
          </Button>
          <Button onClick={handleConfirm} variant="contained" disabled={submitting}>
            {submitting ? <CircularProgress size={18} /> : 'Confirm'}
          </Button>
        </DialogActions>
      </Dialog>
    </AdminLayout>
  );
}
