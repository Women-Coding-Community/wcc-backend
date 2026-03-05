import { Box, Button, IconButton, Paper, TextField, Typography } from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import AddIcon from '@mui/icons-material/Add';
import { Controller, useFieldArray } from 'react-hook-form';
import { FormSectionProps } from './types';

export default function ResourcesSection({ control }: Pick<FormSectionProps, 'control'>) {
  const { fields, append, remove } = useFieldArray({
    control,
    name: 'links',
  });

  return (
    <Paper variant="outlined" sx={{ p: 3 }}>
      <Typography variant="h6" sx={{ mb: 2 }}>
        Resources
      </Typography>

      <Controller
        name="books"
        control={control}
        render={({ field }) => (
          <TextField
            {...field}
            fullWidth
            multiline
            rows={3}
            label="Recommend Books"
            placeholder="List recommended books..."
            sx={{ mb: 3 }}
          />
        )}
      />

      <Box
        sx={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          mb: 2,
        }}
      >
        <Typography variant="subtitle1" sx={{ fontWeight: 600 }}>
          Links
        </Typography>
      </Box>

      {fields.map((field, index) => (
        <Box key={field.id} sx={{ display: 'flex', gap: 1, mb: 2 }}>
          <Controller
            name={`links.${index}.title`}
            control={control}
            render={({ field }) => (
              <TextField {...field} label="Title" placeholder="Link title" sx={{ flex: 1 }} />
            )}
          />
          <Controller
            name={`links.${index}.uri`}
            control={control}
            render={({ field }) => (
              <TextField {...field} label="URL" placeholder="https://..." sx={{ flex: 2 }} />
            )}
          />
          <IconButton color="error" onClick={() => remove(index)} sx={{ alignSelf: 'center' }}>
            <DeleteIcon />
          </IconButton>
        </Box>
      ))}

      <Button
        size="small"
        startIcon={<AddIcon />}
        onClick={() => append({ title: '', uri: '' })}
        fullWidth
        variant="outlined"
      >
        Add Link
      </Button>
    </Paper>
  );
}
