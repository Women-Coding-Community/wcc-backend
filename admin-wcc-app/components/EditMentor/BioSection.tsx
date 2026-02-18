import { TextField, Paper, Typography } from '@mui/material';
import { Controller } from 'react-hook-form';
import { FormSectionProps } from './types';

export default function BioSection({ control, errors }: FormSectionProps) {
  return (
    <Paper variant="outlined" sx={{ p: 3 }}>
      <Typography variant="h6" sx={{ mb: 2 }}>
        Bio
      </Typography>
      <Controller
        name="bio"
        control={control}
        render={({ field }) => (
          <TextField
            {...field}
            fullWidth
            required
            multiline
            rows={4}
            error={!!errors.bio}
            helperText={errors.bio?.message}
            placeholder="Enter a detailed bio..."
          />
        )}
      />
    </Paper>
  );
}
