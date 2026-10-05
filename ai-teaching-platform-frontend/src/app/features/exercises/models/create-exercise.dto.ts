import {ExerciseType} from './exercise.model';
import {ExerciseContent} from './exercise-types/exercise-content.model';

export interface CreateExerciseDto{
  lessonIdList: string[],
  type: ExerciseType,
  title: string,
  instructions: string,
  correctAnswers:boolean,
  content: ExerciseContent
}
