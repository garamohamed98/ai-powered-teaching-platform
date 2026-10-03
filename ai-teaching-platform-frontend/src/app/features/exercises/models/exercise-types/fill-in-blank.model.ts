import {ExerciseContent} from './exercise-content.model';

export interface FillInBlankSentence {
  text: string;
  answer: string[];
}

export interface FillInBlankContent extends ExerciseContent{
  sentences: FillInBlankSentence[];
}
