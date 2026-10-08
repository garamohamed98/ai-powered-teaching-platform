import {ExerciseType} from './exercise.model';
import {ExerciseContent} from './exercise-types/exercise-content.model';

export interface ExerciseAttempt{
  id:string;
  exerciseAttemptId:string;
  title:string;
  type:ExerciseType;
  instructions:string;
  content: ExerciseContent;
}
