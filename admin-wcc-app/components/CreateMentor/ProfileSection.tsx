import {
  TextField,
  MenuItem,
  Chip,
  FormControl,
  InputLabel,
  Select,
  Autocomplete,
  Typography,
} from '@mui/material';
import Grid from '@mui/material/Grid2';
import { Controller } from 'react-hook-form';
import { SPOKEN_LANGUAGES } from '@/lib/languages';
import { PROFILE_STATUSES } from '@/lib/profileStatuses';
import { FormSectionProps } from './types';

export default function ProfileSection({ control, errors }: FormSectionProps) {
  return (
    <Grid size={12}>
      <Typography variant="h6" sx={{ mb: 2, mt: 3 }}>
        Profile Information
      </Typography>
      <Grid container spacing={3}>
        <Grid size={12}>
          <Controller
            name="profileStatus"
            control={control}
            render={({ field }) => (
              <FormControl fullWidth required error={!!errors.profileStatus}>
                <InputLabel>Profile Status</InputLabel>
                <Select {...field} label="Profile Status">
                  {PROFILE_STATUSES.map((status) => (
                    <MenuItem key={status.value} value={status.value}>
                      {status.label}
                    </MenuItem>
                  ))}
                </Select>
                {errors.profileStatus && (
                  <Typography variant="caption" color="error" sx={{ mt: 0.5, ml: 1.5 }}>
                    {errors.profileStatus.message}
                  </Typography>
                )}
              </FormControl>
            )}
          />
        </Grid>

        <Grid size={12}>
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
                label="Bio"
                error={!!errors.bio}
                helperText={errors.bio?.message}
                placeholder="Enter a detailed bio for the mentor..."
              />
            )}
          />
        </Grid>

        <Grid size={12}>
          <Controller
            name="spokenLanguages"
            control={control}
            render={({ field }) => (
              <Autocomplete
                multiple
                options={SPOKEN_LANGUAGES}
                value={field.value}
                onChange={(_, newValue) => field.onChange(newValue)}
                renderInput={(params) => (
                  <TextField {...params} label="Spoken Languages" placeholder="Select languages" />
                )}
                renderTags={(value, getTagProps) =>
                  value.map((option, index) => (
                    <Chip
                      label={option}
                      {...getTagProps({ index })}
                      key={option}
                      color="secondary"
                    />
                  ))
                }
              />
            )}
          />
        </Grid>
      </Grid>
    </Grid>
  );
}
