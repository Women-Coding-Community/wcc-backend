import React from 'react';
import {Avatar, Box, Card, CardContent, Chip, Stack, Typography} from '@mui/material';
import {MenteeApplicationItem} from '@/types/mentorship';

interface MenteeApplicationCardProps {
  application: MenteeApplicationItem;
  showRejectionReason?: boolean;
}

export default function MenteeApplicationCard({
                                                application,
                                                showRejectionReason = false,
                                              }: MenteeApplicationCardProps) {
  const getStatusColor = (status: string) => {
    switch (status.toUpperCase()) {
      case 'MATCHED':
      case 'MENTOR_ACCEPTED':
        return 'success';
      case 'PENDING':
      case 'MENTOR_REVIEWING':
        return 'info';
      case 'REJECTED':
      case 'MENTOR_DECLINED':
      case 'DROPPED':
      case 'EXPIRED':
        return 'error';
      default:
        return 'default';
    }
  };

  return (
      <Card sx={{mb: 2}}>
        <CardContent>
          <Stack direction="row" spacing={2} alignItems="center">
            <Avatar sx={{bgcolor: 'secondary.main'}}>
              {application.mentee.fullName.substring(0, 1)}
            </Avatar>
            <Box sx={{flex: 1}}>
              <Typography variant="subtitle1" sx={{fontWeight: 600}}>
                {application.mentee.fullName}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                {application.mentee.position || 'No position provided'}
              </Typography>
            </Box>
            <Stack alignItems="flex-end" spacing={1}>
              <Chip
                  label={application.status}
                  color={getStatusColor(application.status)}
                  size="small"
              />
              {application.appliedAt && (
                  <Typography variant="caption" color="text.secondary">
                    Applied: {new Date(application.appliedAt).toLocaleDateString()}
                  </Typography>
              )}
            </Stack>
          </Stack>

          {showRejectionReason && application.rejectionReason && (
              <Box sx={{mt: 2, p: 1, bgcolor: 'error.light', borderRadius: 1}}>
                <Typography variant="body2" color="error.contrastText">
                  <strong>Reason:</strong> {application.rejectionReason}
                </Typography>
              </Box>
          )}
        </CardContent>
      </Card>
  );
}
