import { useRef } from 'react';
import { Avatar, Box, Button, Chip, CircularProgress, Paper, Typography } from '@mui/material';

interface ProfilePictureSectionProps {
  fullName: string;
  profileStatus: string;
  imageUrl?: string;
  onPictureChange?: (file: File) => void;
  uploading?: boolean;
}

export default function ProfilePictureSection({
  fullName,
  profileStatus,
  imageUrl,
  onPictureChange,
  uploading = false,
}: ProfilePictureSectionProps) {
  const fileInputRef = useRef<HTMLInputElement>(null);

  const statusColor =
    profileStatus === 'ACTIVE' ? 'success' : profileStatus === 'PENDING' ? 'warning' : 'default';
  const statusLabel =
    profileStatus === 'ACTIVE'
      ? 'Approved'
      : profileStatus.charAt(0) + profileStatus.slice(1).toLowerCase();

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file && onPictureChange) {
      onPictureChange(file);
    }
    e.target.value = '';
  };

  return (
    <Paper variant="outlined" sx={{ p: 3 }}>
      <Typography variant="h6" sx={{ mb: 2 }}>
        Profile Picture & Status
      </Typography>
      <Box sx={{ display: 'flex', gap: 3 }}>
        <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 1 }}>
          <Avatar
            src={imageUrl}
            sx={{ width: 120, height: 120, bgcolor: 'primary.main', fontSize: 48 }}
          >
            {(fullName || '?').charAt(0)}
          </Avatar>
          <input
            type="file"
            accept="image/*"
            ref={fileInputRef}
            style={{ display: 'none' }}
            onChange={handleFileChange}
          />
          <Button
            variant="outlined"
            size="small"
            disabled={uploading}
            startIcon={uploading ? <CircularProgress size={14} /> : undefined}
            onClick={() => fileInputRef.current?.click()}
          >
            {uploading ? 'Uploading...' : 'Change Picture'}
          </Button>
        </Box>
        <Box
          sx={{
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'flex-start',
            gap: 1,
          }}
        >
          <Typography variant="h6">Profile Status</Typography>
          <Chip label={statusLabel} color={statusColor} sx={{ display: 'flex' }} />
        </Box>
      </Box>
    </Paper>
  );
}
