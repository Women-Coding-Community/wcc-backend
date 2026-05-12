import React, { useState } from 'react';
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  CircularProgress,
  Grid,
  Typography,
} from '@mui/material';
import { MentorMatches } from '@/types/mentorship';
import MentorInfoCard from './MentorInfoCard';
import MenteeCard from './MenteeCard';
import { createManualMatch } from '@/services/mentorshipService';

interface MatchCardProps {
  match: MentorMatches;
  cycleId: number;
  token: string;
  onMenteeMatched: (mentorId: string | number, menteeId: number) => void;
}

export default function MatchCard({ match, cycleId, token, onMenteeMatched }: MatchCardProps) {
  const [loadingMenteeId, setLoadingMenteeId] = useState<number | null>(null);
  const [error, setError] = useState<{ id: number; message: string } | null>(null);
  const [successId, setSuccessId] = useState<number | null>(null);

  const handleMatch = async (menteeId: number) => {
    setLoadingMenteeId(menteeId);
    setError(null);
    setSuccessId(null);
    try {
      await createManualMatch(menteeId, cycleId, match.mentor.id, token);
      setSuccessId(menteeId);
      onMenteeMatched(match.mentor.id, menteeId);
    } catch (e) {
      setError({ id: menteeId, message: e instanceof Error ? e.message : 'Failed to match' });
    } finally {
      setLoadingMenteeId(null);
    }
  };

  return (
    <Card sx={{ mb: 4, borderLeft: 6, borderColor: 'primary.main' }}>
      <CardContent>
        <Grid container spacing={4}>
          <Grid item xs={12} md={5}>
            <Typography variant="h6" gutterBottom color="primary">
              Mentor #{match.mentor.id}
            </Typography>
            <MentorInfoCard mentor={match.mentor} />
            <Box sx={{ mt: 2 }}>
              <Typography variant="body2" color="text.secondary">
                Review mentor profile and availability before matching.
              </Typography>
            </Box>
          </Grid>

          <Grid item xs={12} md={7}>
            <Typography variant="h6" gutterBottom color="secondary">
              Recommended {match.mentees.length || 0} mentees
            </Typography>
            {match.mentees.length > 0 ? (
              match.mentees.map((suggestion) => (
                <Box key={suggestion.mentee.id} sx={{ mb: 2 }}>
                  <MenteeCard mentee={suggestion.mentee} score={suggestion.score} />

                  <Box
                    sx={{
                      display: 'flex',
                      flexDirection: 'column',
                      alignItems: 'flex-end',
                      mt: -1,
                      mb: 1,
                    }}
                  >
                    {suggestion.applicationStatus ? (
                      <Chip
                        label={suggestion.applicationStatus}
                        color={suggestion.applicationStatus === 'MATCHED' ? 'success' : 'info'}
                        size="small"
                        sx={{ mt: 1 }}
                      />
                    ) : (
                      <Button
                        size="small"
                        variant="contained"
                        color="primary"
                        disabled={loadingMenteeId !== null}
                        onClick={() => handleMatch(suggestion.mentee.id)}
                        startIcon={
                          loadingMenteeId === suggestion.mentee.id ? (
                            <CircularProgress size={16} />
                          ) : null
                        }
                      >
                        Match with {suggestion.mentee.fullName.split(' ')[0]}
                      </Button>
                    )}

                    {successId === suggestion.mentee.id && (
                      <Alert severity="success" sx={{ mt: 1, py: 0, width: '100%' }}>
                        Matched successfully! Status: MENTOR_REVIEWING
                      </Alert>
                    )}
                    {error?.id === suggestion.mentee.id && (
                      <Alert severity="error" sx={{ mt: 1, py: 0, width: '100%' }}>
                        {error.message}
                      </Alert>
                    )}
                  </Box>
                </Box>
              ))
            ) : (
              <Typography color="text.secondary">
                No recommended mentees for this mentor.
              </Typography>
            )}
          </Grid>
        </Grid>
      </CardContent>
    </Card>
  );
}
