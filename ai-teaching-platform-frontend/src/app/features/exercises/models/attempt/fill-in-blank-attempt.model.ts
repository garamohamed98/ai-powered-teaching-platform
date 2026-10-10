import {Attempt} from './attempt.dto';

export interface FillInBlankSentenceAttempt{
  sentenceId: string;
  answer: String;
}

export interface FillInBlankAttempt extends Attempt{
  sentences: FillInBlankSentenceAttempt[];
}
