import { useState } from 'react';
import { Box, Button, Typography } from '@mui/material';

const BIO_TRUNCATE_LENGTH = 260;

interface BioSectionProps {
  bio: string;
}

export default function BioSection({ bio }: BioSectionProps) {
  const [expanded, setExpanded] = useState(false);
  const isTruncated = bio.length > BIO_TRUNCATE_LENGTH;

  return (
    <Box sx={{ mt: 2 }}>
      <Typography variant="body2">
        {isTruncated && !expanded ? bio.substring(0, BIO_TRUNCATE_LENGTH) + '…' : bio}
      </Typography>
      {isTruncated && (
        <Button
          size="small"
          onClick={() => setExpanded((prev) => !prev)}
          sx={{ mt: 0.5, p: 0, minWidth: 0, textTransform: 'none' }}
        >
          {expanded ? 'See less' : 'See more'}
        </Button>
      )}
    </Box>
  );
}
