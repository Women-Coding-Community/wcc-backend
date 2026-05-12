import React, {useEffect, useState} from 'react';
import {Alert, Box, CircularProgress, Tab, Tabs, Typography} from '@mui/material';
import {MenteeApplicationItem} from '@/types/mentorship';
import {getMenteeApplications} from '@/services/mentorshipService';
import MenteeApplicationCard from './MenteeApplicationCard';

interface MentorApplicationsPanelProps {
  mentorId: number;
  cycleId: number;
  token: string;
}

export default function MentorApplicationsPanel({
                                                  mentorId,
                                                  cycleId,
                                                  token,
                                                }: MentorApplicationsPanelProps) {
  const [tab, setTab] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [applications, setApplications] = useState<MenteeApplicationItem[]>([]);

  const tabs = [
    {label: 'Pending', statuses: ['PENDING', 'MENTOR_REVIEWING']},
    {label: 'Accepted', statuses: ['MENTOR_ACCEPTED', 'MATCHED']},
    {
      label: 'Rejected',
      statuses: ['MENTOR_DECLINED', 'REJECTED', 'DROPPED', 'EXPIRED'],
      showReason: true,
    },
  ];

  useEffect(() => {
    const fetchApps = async () => {
      setLoading(true);
      setError(null);
      try {
        const data = await getMenteeApplications(cycleId, tabs[tab].statuses, token, mentorId);
        setApplications(data);
      } catch (e) {
        setError(e instanceof Error ? e.message : 'Failed to fetch applications');
      } finally {
        setLoading(false);
      }
    };

    if (cycleId && token) {
      fetchApps();
    }
  }, [tab, mentorId, cycleId, token]);

  return (
      <Box sx={{mt: 2}}>
        <Typography variant="h6" gutterBottom>
          Mentor's Applications
        </Typography>
        <Tabs value={tab} onChange={(_, v) => setTab(v)} sx={{mb: 2}}>
          {tabs.map((t, i) => (
              <Tab key={i} label={t.label}/>
          ))}
        </Tabs>

        {loading ? (
            <Box sx={{display: 'flex', justifyContent: 'center', p: 3}}>
              <CircularProgress/>
            </Box>
        ) : error ? (
            <Alert severity="error">{error}</Alert>
        ) : applications.length > 0 ? (
            applications.map((app) => (
                <MenteeApplicationCard
                    key={app.menteeId}
                    application={app}
                    showRejectionReason={tabs[tab].showReason}
                />
            ))
        ) : (
            <Typography color="text.secondary">No applications found in this category.</Typography>
        )}
      </Box>
  );
}
