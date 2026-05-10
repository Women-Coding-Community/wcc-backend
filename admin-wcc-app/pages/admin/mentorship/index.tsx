import React, { useEffect, useState } from 'react';
import {
  Alert,
  Box,
  CircularProgress,
  Container,
  Paper,
  Tab,
  Tabs,
  Typography,
} from '@mui/material';
import AdminLayout from '@/components/AdminLayout';
import { getMentorshipRecommendations } from '@/services/mentorshipService';
import { MentorshipRecommendationResponse } from '@/types/mentorship';
import { getStoredToken, isTokenExpired } from '@/lib/auth';
import { useRouter } from 'next/router';
import MatchCard from '@/components/mentorship/MatchCard';
import MenteeCard from '@/components/mentorship/MenteeCard';
import MentorInfoCard from '@/components/mentorship/MentorInfoCard';

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

function CustomTabPanel(props: TabPanelProps) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`simple-tabpanel-${index}`}
      aria-labelledby={`simple-tab-${index}`}
      {...other}
    >
      {value === index && <Box sx={{ py: 3 }}>{children}</Box>}
    </div>
  );
}

export default function MentorshipAdminPage() {
  const router = useRouter();
  const [loading, setLoading] = useState(true);
  const [data, setData] = useState<MentorshipRecommendationResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [tabValue, setTabValue] = useState(0);

  useEffect(() => {
    const token = getStoredToken();
    if (!token || isTokenExpired(token)) {
      router.replace('/login');
      return;
    }

    getMentorshipRecommendations(token)
      .then((res) => {
        setData(respToRecommendationResponse(res));
        setLoading(false);
      })
      .catch((err) => {
        setError(err.message || 'Failed to fetch recommendations');
        setLoading(false);
      });
  }, [router]);

  const respToRecommendationResponse = (resp: any): MentorshipRecommendationResponse => {
    return {
      matchedMentors: resp.matchedMentors || [],
      notMatchedMentors: resp.notMatchedMentors || [],
      notMatchedMentees: resp.notMatchedMentees || [],
    };
  };

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setTabValue(newValue);
  };

  if (loading) {
    return (
      <AdminLayout>
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 8 }}>
          <CircularProgress />
        </Box>
      </AdminLayout>
    );
  }

  return (
    <AdminLayout>
      <Container maxWidth="xl">
        <Typography variant="h4" gutterBottom sx={{ mt: 2, mb: 4 }}>
          Mentorship - Manual Matching
        </Typography>

        {error && (
          <Alert severity="error" sx={{ mb: 4 }}>
            {error}
          </Alert>
        )}

        <Paper sx={{ width: '100%', mb: 4 }}>
          <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
            <Tabs value={tabValue} onChange={handleTabChange} aria-label="mentorship tabs">
              <Tab label={`Recommendations (${data?.matchedMentors.length || 0})`} />
              <Tab label={`Unmatched Mentors (${data?.notMatchedMentors.length || 0})`} />
              <Tab label={`Unmatched Mentees (${data?.notMatchedMentees.length || 0})`} />
            </Tabs>
          </Box>

          <CustomTabPanel value={tabValue} index={0}>
            {data?.matchedMentors.map((match, idx) => (
              <MatchCard key={match.mentor.id || idx} match={match} />
            ))}
            {data?.matchedMentors.length === 0 && (
              <Typography color="text.secondary" align="center">
                No matches found.
              </Typography>
            )}
          </CustomTabPanel>

          <CustomTabPanel value={tabValue} index={1}>
            <Box sx={{ maxWidth: 800, mx: 'auto' }}>
              {data?.notMatchedMentors.map((mentor) => (
                <MentorInfoCard key={mentor.id} mentor={mentor} />
              ))}
              {data?.notMatchedMentors.length === 0 && (
                <Typography color="text.secondary" align="center">
                  All mentors have recommendations.
                </Typography>
              )}
            </Box>
          </CustomTabPanel>

          <CustomTabPanel value={tabValue} index={2}>
            <Box sx={{ maxWidth: 800, mx: 'auto' }}>
              {data?.notMatchedMentees.map((mentee) => (
                <MenteeCard key={mentee.id} mentee={mentee} />
              ))}
              {data?.notMatchedMentees.length === 0 && (
                <Typography color="text.secondary" align="center">
                  All mentees have recommendations.
                </Typography>
              )}
            </Box>
          </CustomTabPanel>
        </Paper>
      </Container>
    </AdminLayout>
  );
}
