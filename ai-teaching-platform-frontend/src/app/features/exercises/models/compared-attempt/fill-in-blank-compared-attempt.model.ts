import {ExerciseContent} from '../exercise-types/exercise-content.model';
import {ComparedAttempt} from './compared-attempt.model';

export interface FillInBlankSentenceComparedAttempt{
  text:string;
  answers: string[];
  submittedAnswers: string;
  isCorrect: boolean;
}

export interface FillInBlankComparedAttempt extends ComparedAttempt{
  sentences: FillInBlankSentenceComparedAttempt[];
}
