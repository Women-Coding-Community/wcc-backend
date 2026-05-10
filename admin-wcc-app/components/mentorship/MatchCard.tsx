import React from 'react';
import { Box, Button, Card, CardContent, Grid, Typography } from '@mui/material';
import { MentorMatches } from '@/types/mentorship';
import MentorInfoCard from './MentorInfoCard';
import MenteeCard from './MenteeCard';

interface MatchCardProps {
  match: MentorMatches;
}

export default function MatchCard({ match }: MatchCardProps) {
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

                  <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: -1, mb: 1 }}>
                    <Button size="small" variant="contained" color="primary">
                      Match with {suggestion.mentee.fullName.split(' ')[0]}
                    </Button>
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
