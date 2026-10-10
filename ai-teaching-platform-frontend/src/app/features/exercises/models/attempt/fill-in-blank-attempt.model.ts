import {Attempt} from './attempt.dto';

export interface FillInBlankSentenceAttempt{
  sentenceId: string;
  answer: String;
}

export interface FillInBlankAttemptModel extends Attempt{
  sentence: FillInBlankSentenceAttempt[];
}
