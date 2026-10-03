import {ExerciseContent} from './exercise-types/exercise-content.model';

export interface Lesson {
  id: number;
  title: string;
}

export type ExerciseType =
  'MULTIPLE_CHOICE' |
  'FILL_IN_BLANK';

export interface Exercise {
  id: string;
  title: string;
  type: ExerciseType;
  lesson: Lesson[];
  instructions: string;
  content: ExerciseContent;
}
