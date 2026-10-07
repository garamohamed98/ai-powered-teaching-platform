import {ExerciseContent} from './exercise-types/exercise-content.model';
import {Lesson} from './lesson.model';

export type ExerciseType =
  'MULTIPLE_CHOICE' |
  'FILL_IN_BLANK';

export interface Exercise {
  id: string;
  title: string;
  type: ExerciseType;
  lessonList: Lesson[];
  instructions: string;
  content: ExerciseContent;
}

export function getExerciseTypeLabel(type: ExerciseType): string {
  const labels: Record<ExerciseType, string> = {
    MULTIPLE_CHOICE: 'Multiple Choice',
    FILL_IN_BLANK: 'Fill in the Blank'
  };

  return labels[type];
}
