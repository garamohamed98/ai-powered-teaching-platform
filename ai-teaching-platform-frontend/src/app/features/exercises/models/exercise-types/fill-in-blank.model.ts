import {ExerciseContent} from './exercise-content.model';

export interface FillInBlankSentence {
  id: string;
  text: string;
  answer: string[];
}

export interface FillInBlankContent extends ExerciseContent{
  sentences: FillInBlankSentence[];
}
