import {ExerciseType} from './exercise.model';

export interface GenerateExerciseDto{
  lessonIdList: string[];
  type: ExerciseType
}
