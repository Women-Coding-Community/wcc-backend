import React, {useEffect, useState} from 'react';
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
import {getMenteeApplications, getMentorshipRecommendations} from '@/services/mentorshipService';
import {MenteeApplicationItem, MentorshipRecommendationResponse} from '@/types/mentorship';
import {getStoredToken, isTokenExpired} from '@/lib/auth';
import {useRouter} from 'next/router';
import MatchCard from '@/components/mentorship/MatchCard';
import MenteeCard from '@/components/mentorship/MenteeCard';
import MentorInfoCard from '@/components/mentorship/MentorInfoCard';
import MenteeApplicationCard from '@/components/mentorship/MenteeApplicationCard';

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

function CustomTabPanel(props: TabPanelProps) {
  const {children, value, index, ...other} = props;

  return (
      <div
          role="tabpanel"
          hidden={value !== index}
          id={`simple-tabpanel-${index}`}
          aria-labelledby={`simple-tab-${index}`}
          {...other}
      >
        {value === index && <Box sx={{py: 3}}>{children}</Box>}
      </div>
  );
}

export default function MentorshipAdminPage() {
  const router = useRouter();
  const [loading, setLoading] = useState(true);
  const [data, setData] = useState<MentorshipRecommendationResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [tabValue, setTabValue] = useState(0);

  // Application tabs state
  const [pendingApps, setPendingApps] = useState<MenteeApplicationItem[]>([]);
  const [acceptedApps, setAcceptedApps] = useState<MenteeApplicationItem[]>([]);
  const [rejectedApps, setRejectedApps] = useState<MenteeApplicationItem[]>([]);
  const [appsLoading, setAppsLoading] = useState<Record<number, boolean>>({});
  const [appsError, setAppsError] = useState<Record<number, string>>({});

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

  useEffect(() => {
    if (tabValue < 3 || loading) return;

    const token = getStoredToken();
    if (!token) return;

    const cycleId = 1; // Default to 1, in real scenario we would get it from data
    const statusMap: Record<number, string[]> = {
      3: ['PENDING', 'MENTOR_REVIEWING'],
      4: ['MENTOR_ACCEPTED', 'MATCHED'],
      5: ['MENTOR_DECLINED', 'REJECTED', 'DROPPED', 'EXPIRED'],
    };

    const setterMap: Record<
        number,
        React.Dispatch<React.SetStateAction<MenteeApplicationItem[]>>
    > = {
      3: setPendingApps,
      4: setAcceptedApps,
      5: setRejectedApps,
    };

    if (setterMap[tabValue]) {
      setAppsLoading((prev) => ({...prev, [tabValue]: true}));
      setAppsError((prev) => ({...prev, [tabValue]: ''}));

      getMenteeApplications(cycleId, statusMap[tabValue], token)
      .then(setterMap[tabValue])
      .catch((err) => setAppsError((prev) => ({...prev, [tabValue]: err.message})))
      .finally(() => setAppsLoading((prev) => ({...prev, [tabValue]: false})));
    }
  }, [tabValue, loading]);

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

  const handleMenteeMatched = (mentorId: string | number, menteeId: number) => {
    // Refresh recommendations to update applicationStatus and matched/unmatched lists
    const token = getStoredToken();
    if (token) {
      getMentorshipRecommendations(token).then((res) => setData(respToRecommendationResponse(res)));
    }
  };

  if (loading) {
    return (
        <AdminLayout>
          <Box sx={{display: 'flex', justifyContent: 'center', mt: 8}}>
            <CircularProgress/>
          </Box>
        </AdminLayout>
    );
  }

  return (
      <AdminLayout>
        <Container maxWidth="xl">
          <Typography variant="h4" gutterBottom sx={{mt: 2, mb: 4}}>
            Mentorship - Manual Matching
          </Typography>

          {error && (
              <Alert severity="error" sx={{mb: 4}}>
                {error}
              </Alert>
          )}

          <Paper sx={{width: '100%', mb: 4}}>
            <Box sx={{borderBottom: 1, borderColor: 'divider'}}>
              <Tabs value={tabValue} onChange={handleTabChange} aria-label="mentorship tabs">
                <Tab label={`Recommendations (${data?.matchedMentors.length || 0})`}/>
                <Tab label={`Unmatched Mentors (${data?.notMatchedMentors.length || 0})`}/>
                <Tab label={`Unmatched Mentees (${data?.notMatchedMentees.length || 0})`}/>
                <Tab label="Pending Review"/>
                <Tab label="Accepted"/>
                <Tab label="Rejected"/>
              </Tabs>
            </Box>

            <CustomTabPanel value={tabValue} index={0}>
              {data?.matchedMentors.map((match, idx) => (
                  <MatchCard
                      key={match.mentor.id || idx}
                      match={match}
                      cycleId={1}
                      token={getStoredToken() || ''}
                      onMenteeMatched={handleMenteeMatched}
                  />
              ))}
              {data?.matchedMentors.length === 0 && (
                  <Typography color="text.secondary" align="center">
                    No matches found.
                  </Typography>
              )}
            </CustomTabPanel>

            <CustomTabPanel value={tabValue} index={1}>
              <Box sx={{maxWidth: 800, mx: 'auto'}}>
                {data?.notMatchedMentors.map((mentor) => (
                    <MentorInfoCard key={mentor.id} mentor={mentor}/>
                ))}
                {data?.notMatchedMentors.length === 0 && (
                    <Typography color="text.secondary" align="center">
                      All mentors have recommendations.
                    </Typography>
                )}
              </Box>
            </CustomTabPanel>

            <CustomTabPanel value={tabValue} index={2}>
              <Box sx={{maxWidth: 800, mx: 'auto'}}>
                {data?.notMatchedMentees.map((mentee) => (
                    <MenteeCard key={mentee.id} mentee={mentee}/>
                ))}
                {data?.notMatchedMentees.length === 0 && (
                    <Typography color="text.secondary" align="center">
                      All mentees have recommendations.
                    </Typography>
                )}
              </Box>
            </CustomTabPanel>

            <CustomTabPanel value={tabValue} index={3}>
              <Box sx={{maxWidth: 800, mx: 'auto'}}>
                {appsLoading[3] ? (
                    <CircularProgress/>
                ) : appsError[3] ? (
                    <Alert severity="error">{appsError[3]}</Alert>
                ) : (
                    pendingApps.map((app) => (
                        <MenteeApplicationCard key={app.menteeId} application={app}/>
                    ))
                )}
                {!appsLoading[3] && pendingApps.length === 0 && (
                    <Typography>No pending applications.</Typography>
                )}
              </Box>
            </CustomTabPanel>

            <CustomTabPanel value={tabValue} index={4}>
              <Box sx={{maxWidth: 800, mx: 'auto'}}>
                {appsLoading[4] ? (
                    <CircularProgress/>
                ) : appsError[4] ? (
                    <Alert severity="error">{appsError[4]}</Alert>
                ) : (
                    acceptedApps.map((app) => (
                        <MenteeApplicationCard key={app.menteeId} application={app}/>
                    ))
                )}
                {!appsLoading[4] && acceptedApps.length === 0 && (
                    <Typography>No accepted applications.</Typography>
                )}
              </Box>
            </CustomTabPanel>

            <CustomTabPanel value={tabValue} index={5}>
              <Box sx={{maxWidth: 800, mx: 'auto'}}>
                {appsLoading[5] ? (
                    <CircularProgress/>
                ) : appsError[5] ? (
                    <Alert severity="error">{appsError[5]}</Alert>
                ) : (
                    rejectedApps.map((app) => (
                        <MenteeApplicationCard key={app.menteeId} application={app}
                                               showRejectionReason/>
                    ))
                )}
                {!appsLoading[5] && rejectedApps.length === 0 && (
                    <Typography>No rejected applications.</Typography>
                )}
              </Box>
            </CustomTabPanel>
          </Paper>
        </Container>
      </AdminLayout>
  );
}
