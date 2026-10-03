import {ExerciseContent} from './exercise-content.model';

export interface MultipleChoiceContent extends ExerciseContent{
  question: string;
  options: string[];
  correctAnswer: string;
}
