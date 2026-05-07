import { useRef } from 'react';
import { Avatar, Box, Button, CircularProgress, Paper, Typography } from '@mui/material';

interface ProfilePictureSectionProps {
  fullName: string;
  imageUrl?: string;
  onPictureChange?: (file: File) => void;
  uploading?: boolean;
}

export default function ProfilePictureSection({
  fullName,
  imageUrl,
  onPictureChange,
  uploading = false,
}: ProfilePictureSectionProps) {
  const fileInputRef = useRef<HTMLInputElement>(null);

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
        Profile Picture
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
            disabled={true}
            startIcon={uploading ? <CircularProgress size={14} /> : undefined}
            onClick={() => fileInputRef.current?.click()}
            title="Picture upload is not available yet"
          >
            Change Picture
          </Button>
        </Box>
      </Box>
    </Paper>
  );
}
