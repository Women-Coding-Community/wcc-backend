import { TextField, Chip, Autocomplete } from '@mui/material';
import Grid from '@mui/material/Grid2';
import { Controller } from 'react-hook-form';
import { MENTORSHIP_TYPES } from '@/lib/mentorshipTypes';
import { FormSectionProps } from './types';

export default function MentorshipSection({ control, errors }: FormSectionProps) {
  return (
    <>
      <Grid size={12}>
        <Controller
          name="mentorshipType"
          control={control}
          render={({ field }) => (
            <Autocomplete
              multiple
              options={MENTORSHIP_TYPES}
              getOptionLabel={(option) =>
                typeof option === 'string'
                  ? MENTORSHIP_TYPES.find((t) => t.value === option)?.label || option
                  : option.label
              }
              value={MENTORSHIP_TYPES.filter((t) => field.value.includes(t.value))}
              onChange={(_, newValue) =>
                field.onChange(newValue.map((v) => (typeof v === 'string' ? v : v.value)))
              }
              isOptionEqualToValue={(option, value) =>
                option.value === (typeof value === 'string' ? value : value.value)
              }
              renderInput={(params) => (
                <TextField
                  {...params}
                  label="Mentorship Type *"
                  placeholder="Select mentorship types"
                  error={!!errors.mentorshipType}
                  helperText={errors.mentorshipType?.message}
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
          name="idealMentee"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              fullWidth
              required
              multiline
              rows={3}
              label="Ideal Mentee"
              error={!!errors.idealMentee}
              helperText={errors.idealMentee?.message}
              placeholder="Describe your ideal mentee and what you're looking for in a mentoring relationship..."
            />
          )}
        />
      </Grid>

      <Grid size={12}>
        <Controller
          name="additionalInfo"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              fullWidth
              multiline
              rows={3}
              label="Additional Information"
              placeholder="Any additional information about your mentoring approach or preferences..."
            />
          )}
        />
      </Grid>
    </>
  );
}
