import React from 'react';
import { Alert, Avatar, Box, Card, CardContent, Chip, Stack, Typography } from '@mui/material';
import {
  Business as BusinessIcon,
  Public as PublicIcon,
  Work as WorkIcon,
} from '@mui/icons-material';
import { MenteeApplicationItem } from '@/types/mentorship';

interface MenteeApplicationCardProps {
  application: MenteeApplicationItem;
  showRejectionReason?: boolean;
}

const STATUS_COLOR: Record<string, 'default' | 'warning' | 'success' | 'error' | 'info'> = {
  PENDING: 'warning',
  MENTOR_REVIEWING: 'info',
  MENTOR_ACCEPTED: 'success',
  MATCHED: 'success',
  MENTOR_DECLINED: 'error',
  REJECTED: 'error',
  DROPPED: 'error',
  EXPIRED: 'default',
};

export default function MenteeApplicationCard({
  application,
  showRejectionReason = false,
}: MenteeApplicationCardProps) {
  const { mentee, status, rejectionReason, appliedAt } = application;
  const location = [mentee.city, mentee.country?.countryName].filter(Boolean).join(', ');
  const statusColor = STATUS_COLOR[status] ?? 'default';

  return (
    <Card variant="outlined" sx={{ mb: 2 }}>
      <CardContent>
        <Stack direction="row" spacing={2} alignItems="flex-start">
          <Avatar
            src={mentee.images?.[0]?.path}
            alt={mentee.fullName}
            sx={{ width: 48, height: 48 }}
          >
            {mentee.fullName.charAt(0)}
          </Avatar>

          <Box sx={{ flex: 1 }}>
            <Stack direction="row" spacing={1} alignItems="center" flexWrap="wrap">
              <Typography variant="h6">{mentee.fullName}</Typography>
              <Chip label={status.replace(/_/g, ' ')} color={statusColor} size="small" />
            </Stack>

            <Stack
              direction="row"
              spacing={1}
              alignItems="center"
              color="text.secondary"
              sx={{ mt: 0.5 }}
            >
              <WorkIcon fontSize="small" />
              <Typography variant="body2">{mentee.position || 'N/A'}</Typography>
              <BusinessIcon fontSize="small" />
              <Typography variant="body2">{mentee.companyName || 'N/A'}</Typography>
              <PublicIcon fontSize="small" />
              <Typography variant="body2">{location || 'N/A'}</Typography>
            </Stack>

            {mentee.skills?.areas && mentee.skills.areas.length > 0 && (
              <Stack direction="row" spacing={0.5} flexWrap="wrap" useFlexGap sx={{ mt: 1 }}>
                {mentee.skills.areas.slice(0, 4).map((a) => (
                  <Chip
                    key={a.technicalArea}
                    label={a.technicalArea.replace(/_/g, ' ')}
                    size="small"
                    color="primary"
                    variant="outlined"
                  />
                ))}
                {mentee.skills.areas.length > 4 && (
                  <Chip
                    label={`+${mentee.skills.areas.length - 4} more`}
                    size="small"
                    variant="outlined"
                  />
                )}
              </Stack>
            )}

            {appliedAt && (
              <Typography
                variant="caption"
                color="text.secondary"
                sx={{ display: 'block', mt: 0.5 }}
              >
                Applied: {new Date(appliedAt).toLocaleDateString()}
              </Typography>
            )}

            {showRejectionReason && rejectionReason && (
              <Alert severity="error" sx={{ mt: 1 }}>
                <strong>Rejection reason:</strong> {rejectionReason}
              </Alert>
            )}
          </Box>
        </Stack>
      </CardContent>
    </Card>
  );
}
