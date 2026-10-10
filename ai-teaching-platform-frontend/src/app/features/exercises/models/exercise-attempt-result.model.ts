import {ExerciseType} from './exercise.model';
import {ComparedAttempt} from './compared-attempt/compared-attempt.model';

export interface ExerciseAttemptResult{
  attemptId: string;
  exerciseId: string;
  lessonIdList: string[];
  type: ExerciseType;
  title: string;
  instructions: string;
  comparedAnswer: ComparedAttempt;
  score: number;
  aiFeedback: string;
  timeTaken: number;
}
