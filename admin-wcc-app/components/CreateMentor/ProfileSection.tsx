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
import { MEMBER_TYPES } from '@/lib/memberTypes';
import { FormSectionProps } from './types';

export default function ProfileSection({ control, errors }: FormSectionProps) {
  return (
    <>
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
          name="memberTypes"
          control={control}
          render={({ field }) => (
            <Autocomplete
              multiple
              options={MEMBER_TYPES}
              getOptionLabel={(option) =>
                typeof option === 'string'
                  ? MEMBER_TYPES.find((t) => t.value === option)?.label || option
                  : option.label
              }
              value={MEMBER_TYPES.filter((t) => field.value.includes(t.value))}
              onChange={(_, newValue) =>
                field.onChange(newValue.map((v) => (typeof v === 'string' ? v : v.value)))
              }
              isOptionEqualToValue={(option, value) =>
                option.value === (typeof value === 'string' ? value : value.value)
              }
              renderInput={(params) => (
                <TextField
                  {...params}
                  label="Member Types *"
                  placeholder="Select member types"
                  error={!!errors.memberTypes}
                  helperText={errors.memberTypes?.message}
                />
              )}
              renderTags={(value, getTagProps) =>
                value.map((option, index) => (
                  <Chip
                    label={option.label}
                    {...getTagProps({ index })}
                    key={option.value}
                    color="secondary"
                  />
                ))
              }
            />
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
                  <Chip label={option} {...getTagProps({ index })} key={option} color="secondary" />
                ))
              }
            />
          )}
        />
      </Grid>
    </>
  );
}
