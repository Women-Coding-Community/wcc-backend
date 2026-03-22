import { TextField, Chip, Autocomplete, Typography } from '@mui/material';
import Grid from '@mui/material/Grid2';
import { Controller } from 'react-hook-form';
import { TECHNICAL_AREAS } from '@/lib/technicalAreas';
import { PROGRAMMING_LANGUAGES } from '@/lib/programmingLanguages';
import { MENTORSHIP_FOCUS_AREAS } from '@/lib/mentorshipFocusAreas';
import { FormSectionProps } from './types';

export default function SkillsSection({ control, errors }: FormSectionProps) {
  return (
    <Grid size={12}>
      <Typography variant="h6" sx={{ mb: 2, mt: 3 }}>
        Skills & Experience
      </Typography>
      <Grid container spacing={3}>
        <Grid size={12}>
          <Controller
            name="yearsExperience"
            control={control}
            render={({ field: { onChange, value, ...field } }) => (
              <TextField
                {...field}
                value={value}
                onChange={(e) => onChange(e.target.value === '' ? '' : Number(e.target.value))}
                fullWidth
                required
                type="number"
                label="Years of Experience"
                error={!!errors.yearsExperience}
                helperText={errors.yearsExperience?.message}
                slotProps={{ htmlInput: { min: 0, max: 50 } }}
              />
            )}
          />
        </Grid>

        <Grid size={12}>
          <Controller
            name="technicalAreas"
            control={control}
            render={({ field }) => (
              <Autocomplete
                multiple
                options={TECHNICAL_AREAS}
                getOptionLabel={(option) =>
                  typeof option === 'string'
                    ? TECHNICAL_AREAS.find((a) => a.value === option)?.label || option
                    : option.label
                }
                value={TECHNICAL_AREAS.filter((a) => field.value.includes(a.value))}
                onChange={(_, newValue) =>
                  field.onChange(newValue.map((v) => (typeof v === 'string' ? v : v.value)))
                }
                isOptionEqualToValue={(option, value) =>
                  option.value === (typeof value === 'string' ? value : value.value)
                }
                renderInput={(params) => (
                  <TextField
                    {...params}
                    label="Technical Areas *"
                    placeholder="Select technical areas"
                    error={!!errors.technicalAreas}
                    helperText={errors.technicalAreas?.message}
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
            name="programmingLanguages"
            control={control}
            render={({ field }) => (
              <Autocomplete
                multiple
                options={PROGRAMMING_LANGUAGES}
                getOptionLabel={(option) =>
                  typeof option === 'string'
                    ? PROGRAMMING_LANGUAGES.find((l) => l.value === option)?.label || option
                    : option.label
                }
                value={PROGRAMMING_LANGUAGES.filter((l) => field.value.includes(l.value))}
                onChange={(_, newValue) =>
                  field.onChange(newValue.map((v) => (typeof v === 'string' ? v : v.value)))
                }
                isOptionEqualToValue={(option, value) =>
                  option.value === (typeof value === 'string' ? value : value.value)
                }
                renderInput={(params) => (
                  <TextField
                    {...params}
                    label="Programming Languages *"
                    placeholder="Select programming languages"
                    error={!!errors.programmingLanguages}
                    helperText={errors.programmingLanguages?.message}
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
            name="mentorshipFocus"
            control={control}
            render={({ field }) => (
              <Autocomplete
                multiple
                options={MENTORSHIP_FOCUS_AREAS}
                getOptionLabel={(option) =>
                  typeof option === 'string'
                    ? MENTORSHIP_FOCUS_AREAS.find((f) => f.value === option)?.label || option
                    : option.label
                }
                value={MENTORSHIP_FOCUS_AREAS.filter((f) => field.value.includes(f.value))}
                onChange={(_, newValue) =>
                  field.onChange(newValue.map((v) => (typeof v === 'string' ? v : v.value)))
                }
                isOptionEqualToValue={(option, value) =>
                  option.value === (typeof value === 'string' ? value : value.value)
                }
                renderInput={(params) => (
                  <TextField
                    {...params}
                    label="Mentorship Focus Areas *"
                    placeholder="Select focus areas"
                    error={!!errors.mentorshipFocus}
                    helperText={errors.mentorshipFocus?.message}
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
      </Grid>
    </Grid>
  );
}
