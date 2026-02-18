import { Control, FieldErrors } from 'react-hook-form';
import { EditMentorFormData } from './schema';

export type { EditMentorFormData } from './schema';

export interface FormSectionProps {
  control: Control<EditMentorFormData>;
  errors: FieldErrors<EditMentorFormData>;
}
