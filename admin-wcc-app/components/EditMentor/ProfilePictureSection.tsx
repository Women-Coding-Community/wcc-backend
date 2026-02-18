import { Avatar, Box, Button, Chip, Paper, Typography } from '@mui/material';

interface ProfilePictureSectionProps {
  fullName: string;
  profileStatus: string;
  imageUrl?: string;
}

export default function ProfilePictureSection({
  fullName,
  profileStatus,
  imageUrl,
}: ProfilePictureSectionProps) {
  const statusColor =
    profileStatus === 'ACTIVE' ? 'success' : profileStatus === 'PENDING' ? 'warning' : 'default';
  const statusLabel =
    profileStatus === 'ACTIVE'
      ? 'Approved'
      : profileStatus.charAt(0) + profileStatus.slice(1).toLowerCase();

  return (
    <Paper variant="outlined" sx={{ p: 3 }}>
      <Typography variant="h6" sx={{ mb: 2 }}>
        Profile Picture & Status
      </Typography>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 3 }}>
        <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 1 }}>
          <Avatar
            src={imageUrl}
            sx={{ width: 80, height: 80, bgcolor: 'primary.main', fontSize: 32 }}
          >
            {(fullName || '?').charAt(0)}
          </Avatar>
          <Button variant="outlined" size="small">
            Change Picture
          </Button>
        </Box>
        <Box>
          <Typography variant="subtitle2" color="text.secondary">
            Profile Status
          </Typography>
          <Chip label={statusLabel} color={statusColor} size="small" />
        </Box>
      </Box>
    </Paper>
  );
}
