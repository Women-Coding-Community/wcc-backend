import {
  Alert,
  Autocomplete,
  Box,
  Checkbox,
  Chip,
  FormControlLabel,
  Paper,
  TextField,
  Typography,
} from '@mui/material';
import Grid from '@mui/material/Grid2';
import { Controller, useWatch } from 'react-hook-form';
import { MENTORSHIP_TYPE_VALUES, MENTORSHIP_TYPES } from '@/lib/mentorshipTypes';
import { FormSectionProps } from './types';

const MONTHS = [
  'JANUARY',
  'FEBRUARY',
  'MARCH',
  'APRIL',
  'MAY',
  'JUNE',
  'JULY',
  'AUGUST',
  'SEPTEMBER',
  'OCTOBER',
  'NOVEMBER',
  'DECEMBER',
];

const monthLabel = (month: string) => month.charAt(0) + month.slice(1).toLowerCase();

export default function MentorshipAvailabilitySection({
  control,
  errors,
  setValue,
  adHocError,
}: FormSectionProps) {
  const mentorshipType = useWatch({ control, name: 'mentorshipType' });
  const monthAvailability = useWatch({ control, name: 'monthAvailability' });

  const hasAdHocMentorship = mentorshipType?.includes(MENTORSHIP_TYPE_VALUES.AD_HOC);
  const hasLongTermMentorship = mentorshipType?.includes(MENTORSHIP_TYPE_VALUES.LONG_TERM);

  return (
    <Paper variant="outlined" sx={{ p: 3 }}>
      <Typography variant="h6" sx={{ mb: 2 }}>
        Mentorship Availability
      </Typography>
      <Grid container spacing={3}>
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
                    required
                    label="Mentorship Type"
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

        {hasLongTermMentorship && (
          <Grid size={12}>
            <Typography variant="subtitle1" sx={{ mb: 1, fontWeight: 600 }}>
              Long-Term Availability
            </Typography>
            <Grid container spacing={2}>
              <Grid size={{ xs: 12, sm: 6 }}>
                <Controller
                  name="longTermNumMentee"
                  control={control}
                  render={({ field: { onChange, value, ...field } }) => (
                    <TextField
                      {...field}
                      value={value}
                      onChange={(e) => onChange(e.target.value === '' ? 1 : Number(e.target.value))}
                      fullWidth
                      required
                      type="number"
                      label="Number of Mentees"
                      slotProps={{ htmlInput: { min: 1 } }}
                      error={!!errors.longTermNumMentee}
                      helperText={errors.longTermNumMentee?.message}
                    />
                  )}
                />
              </Grid>
              <Grid size={{ xs: 12, sm: 6 }}>
                <Controller
                  name="longTermHours"
                  control={control}
                  render={({ field: { onChange, value, ...field } }) => (
                    <TextField
                      {...field}
                      value={value}
                      onChange={(e) => onChange(e.target.value === '' ? 2 : Number(e.target.value))}
                      fullWidth
                      required
                      type="number"
                      label="Max Hours per Month"
                      slotProps={{ htmlInput: { min: 2 } }}
                      error={!!errors.longTermHours}
                      helperText={errors.longTermHours?.message}
                    />
                  )}
                />
              </Grid>
            </Grid>
          </Grid>
        )}

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
                placeholder="Describe your ideal mentee..."
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
                placeholder="Any additional information about your mentoring approach..."
              />
            )}
          />
        </Grid>

        {hasAdHocMentorship && (
          <Grid size={12}>
            <Typography variant="subtitle1" sx={{ mb: 1, fontWeight: 600 }}>
              Month Availability
            </Typography>
            {adHocError && (
              <Alert severity="warning" sx={{ mb: 2 }}>
                {adHocError}
              </Alert>
            )}
            <Grid container spacing={2}>
              {MONTHS.map((month, index) => (
                <Grid size={{ xs: 12, sm: 4 }} key={month}>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <Controller
                      name={`monthAvailability.${index}.enabled`}
                      control={control}
                      render={({ field }) => (
                        <FormControlLabel
                          control={
                            <Checkbox
                              checked={field.value}
                              onChange={(e) => {
                                field.onChange(e.target.checked);
                                if (e.target.checked && setValue) {
                                  const currentHours = monthAvailability?.[index]?.hours ?? 0;
                                  if (currentHours === 0) {
                                    setValue(`monthAvailability.${index}.hours`, 1, {
                                      shouldValidate: false,
                                    });
                                  }
                                }
                              }}
                              size="small"
                            />
                          }
                          label={monthLabel(month)}
                          sx={{ minWidth: 110 }}
                        />
                      )}
                    />
                    <Controller
                      name={`monthAvailability.${index}.hours`}
                      control={control}
                      render={({ field: { onChange, value, ...field } }) => (
                        <TextField
                          {...field}
                          value={value}
                          onChange={(e) =>
                            onChange(e.target.value === '' ? 0 : Number(e.target.value))
                          }
                          type="number"
                          size="small"
                          label="hours"
                          slotProps={{ htmlInput: { min: 1, max: 200 } }}
                          sx={{ width: 80 }}
                        />
                      )}
                    />
                  </Box>
                </Grid>
              ))}
            </Grid>
          </Grid>
        )}
      </Grid>
    </Paper>
  );
}
