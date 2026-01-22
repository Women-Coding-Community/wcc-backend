import { Control } from 'react-hook-form';
import { MentorFormData } from './schema';

export type { MentorFormData } from './schema';

export interface FormSectionProps {
  control: Control<MentorFormData>;
  errors: Record<string, any>;
}
