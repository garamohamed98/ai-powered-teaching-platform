import {ExerciseType} from './exercise.model';
import {Attempt} from './attempt/attempt.dto';

export interface SubmitExerciseAttemptDto{
  exerciseType: ExerciseType;
  attempt: Attempt
}
