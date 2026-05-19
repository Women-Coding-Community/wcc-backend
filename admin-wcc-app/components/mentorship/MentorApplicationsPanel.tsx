import React, {useEffect, useState} from 'react';
import {Alert, Box, CircularProgress, Divider, Typography} from '@mui/material';
import {getMenteeApplications} from '@/services/mentorshipService';
import {MenteeApplicationItem} from '@/types/mentorship';
import MenteeApplicationCard from './MenteeApplicationCard';

interface MentorApplicationsPanelProps {
  mentorId: number;
  cycleId: number;
  token: string;
}

const ALL_STATUSES = [
  'PENDING',
  'MENTOR_REVIEWING',
  'MENTOR_ACCEPTED',
  'MATCHED',
  'MENTOR_DECLINED',
  'REJECTED',
  'DROPPED',
  'EXPIRED',
];

export default function MentorApplicationsPanel({
                                                  mentorId,
                                                  cycleId,
                                                  token,
                                                }: MentorApplicationsPanelProps) {
  const [applications, setApplications] = useState<MenteeApplicationItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!mentorId || !token) return;

    setLoading(true);
    setError(null);

    getMenteeApplications(cycleId, ALL_STATUSES, token, mentorId)
    .then(setApplications)
    .catch((err) => setError(err.message || 'Failed to load applications'))
    .finally(() => setLoading(false));
  }, [mentorId, cycleId, token]);

  return (
      <Box>
        <Typography variant="h6" gutterBottom>
          Applications for Mentor #{mentorId}
        </Typography>
        <Divider sx={{mb: 2}}/>

        {loading && <CircularProgress/>}
        {error && <Alert severity="error">{error}</Alert>}

        {!loading && !error && applications.length === 0 && (
            <Typography color="text.secondary">No applications found for this mentor.</Typography>
        )}

        {applications.map((app) => (
            <MenteeApplicationCard key={app.menteeId} application={app} showRejectionReason/>
        ))}
      </Box>
  );
}
